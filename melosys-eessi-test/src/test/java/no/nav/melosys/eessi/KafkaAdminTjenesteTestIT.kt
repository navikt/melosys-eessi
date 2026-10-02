package no.nav.melosys.eessi

import no.nav.melosys.eessi.controller.dto.KafkaConsumerResponse
import no.nav.melosys.eessi.integration.oppgave.HentOppgaveDto
import no.nav.melosys.eessi.models.kafkadlq.SedSendtHendelseKafkaDLQ
import no.nav.melosys.eessi.repository.KafkaDLQRepository
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import org.assertj.core.api.Assertions
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.UUID
import kotlin.random.Random
import org.springframework.kafka.test.utils.ContainerTestUtils
import tools.jackson.databind.json.JsonMapper
import tools.jackson.core.type.TypeReference

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
class KafkaAdminTjenesteTestIT : ComponentTestBase() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mockOAuth2Server: MockOAuth2Server

    @Autowired
    private lateinit var jsonMapper: JsonMapper

    @Autowired
    private lateinit var kafkaDLQRepository: KafkaDLQRepository

    @Value("\${melosys.admin.driftsgruppe}")
    private lateinit var driftsgruppeId: String

    @Value("\${melosys.admin.console-klient-id}")
    private lateinit var consoleKlientId: String

    // Klient-ID-en sendes også som azp, fordi mock-oauth2-server kan overskrive claimet med den.
    private fun hentBearerToken(): String {
        return mockOAuth2Server.issueToken(
            "issuer1",
            consoleKlientId,
            DefaultOAuth2TokenCallback(
                issuerId = "issuer1",
                subject = "testbruker",
                audience = listOf("dumbdumb"),
                claims = mapOf(
                    "oid" to "test-oid",
                    "azp" to consoleKlientId,
                    "NAVident" to "test123",
                    "groups" to listOf(driftsgruppeId)
                )
            )
        ).serialize()
    }

    @Test
    fun `hentKafkaConsumers returner informasjon om alle consumere`() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/admin/kafka/consumers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${hentBearerToken()}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val jsonResponse = result.response.contentAsString
        val kafkaConsumerResponses: List<KafkaConsumerResponse> = jsonMapper.readValue(
            jsonResponse, object : TypeReference<List<KafkaConsumerResponse>>() {}
        )

        assertThat(kafkaConsumerResponses).hasSize(4)
    }

    @Test
    fun `stoppOgStartConsumer consumer stopped og startes`() {
        // Test stop
        val stopResult = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/admin/kafka/consumers/oppgaveHendelse/stop")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${hentBearerToken()}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val stopJsonResponse = stopResult.response.contentAsString
        val kafkaConsumerResponseStop: KafkaConsumerResponse = jsonMapper.readValue(
            stopJsonResponse, KafkaConsumerResponse::class.java
        )

        assertThat(kafkaConsumerResponseStop).isNotNull
        assertThat(kafkaConsumerResponseStop.active).isFalse

        // Test start
        val startResult = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/admin/kafka/consumers/oppgaveHendelse/start")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${hentBearerToken()}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val startJsonResponse = startResult.response.contentAsString
        val kafkaConsumerResponseStart: KafkaConsumerResponse = jsonMapper.readValue(
            startJsonResponse, KafkaConsumerResponse::class.java
        )

        assertThat(kafkaConsumerResponseStart).isNotNull
        assertThat(kafkaConsumerResponseStart.active).isTrue
    }

    @Test
    fun `settOffset sender inn offset2 consumer leser på nytt fra offset2`() {
        // Wait for consumer to be assigned partitions before sending messages
        listenerRegistry.getListenerContainer("oppgaveHendelse")?.let {
            ContainerTestUtils.waitForAssignment(it, 1)
        }

        val rinaSaksnummer = Random.nextInt(100000).toString()
        val oppgaveID = Random.nextInt(100000).toString()
        val oppgaveID1 = Random.nextInt(100000).toString()
        val oppgaveID2 = Random.nextInt(100000).toString()
        val oppgaveID3 = Random.nextInt(100000).toString()

        val oppgaveDto = HentOppgaveDto(oppgaveID, "AAPEN", 1)
        val oppgaveDto1 = HentOppgaveDto(oppgaveID1, "AAPEN", 1)
        val oppgaveDto2 = HentOppgaveDto(oppgaveID2, "AAPEN", 1)
        val oppgaveDto3 = HentOppgaveDto(oppgaveID3, "AAPEN", 1)

        `when`(oppgaveConsumer.hentOppgave(oppgaveID)).thenReturn(oppgaveDto)
        `when`(oppgaveConsumer.hentOppgave(oppgaveID1)).thenReturn(oppgaveDto1)
        `when`(oppgaveConsumer.hentOppgave(oppgaveID2)).thenReturn(oppgaveDto2)
        `when`(oppgaveConsumer.hentOppgave(oppgaveID3)).thenReturn(oppgaveDto3)

        val versjon = "1"
        kafkaTemplate.send(lagOppgaveIdentifisertRecord(oppgaveID, versjon, rinaSaksnummer)).get()
        kafkaTemplate.send(lagOppgaveIdentifisertRecord(oppgaveID1, versjon, rinaSaksnummer)).get()
        kafkaTemplate.send(lagOppgaveIdentifisertRecord(oppgaveID2, versjon, rinaSaksnummer)).get()
        kafkaTemplate.send(lagOppgaveIdentifisertRecord(oppgaveID3, versjon, rinaSaksnummer)).get()

        verify(oppgaveConsumer, timeout(5000).times(4)).hentOppgave(anyString())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/admin/kafka/consumers/oppgaveHendelse/seek/2")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${hentBearerToken()}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        // Vi sender 4 meldinger, resetter til offset 2, melding med offset 2 og 3 leses på nytt, totalt hentes oppgave 6 ganger.
        verify(oppgaveConsumer, timeout(6_000).times(6)).hentOppgave(anyString())
    }

    @Test
    fun `slettKafkaMelding sletter eksisterende melding og returnerer 204`() {
        val id = UUID.randomUUID()
        val melding = SedSendtHendelseKafkaDLQ().apply {
            this.id = id
            sedSendtHendelse = mockData.sedHendelse("rinasak", UUID.randomUUID().toString(), FNR)
        }
        kafkaDLQRepository.save(melding)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/admin/kafka/dlq/$id")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${hentBearerToken()}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)

        Assertions.assertThat(kafkaDLQRepository.findById(id)).isEmpty()
    }

    @Test
    fun `slettKafkaMelding returnerer 404 når melding ikke finnes`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/admin/kafka/dlq/${UUID.randomUUID()}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${hentBearerToken()}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `slettKafkaMelding returnerer 400 ved ugyldig uuid`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/admin/kafka/dlq/ikke-en-gyldig-uuid")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${hentBearerToken()}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

}
