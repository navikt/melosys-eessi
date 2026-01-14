package no.nav.melosys.eessi

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.melosys.eessi.controller.dto.KafkaConsumerResponse
import no.nav.melosys.eessi.integration.oppgave.HentOppgaveDto
import no.nav.security.mock.oauth2.MockOAuth2Server
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import kotlin.random.Random

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
class KafkaAdminTjenesteTestIT : ComponentTestBase() {

    companion object {
        private const val API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY"
        private const val GYLDIG_API_NOKKEL = "dummy"
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mockOAuth2Server: MockOAuth2Server

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private fun hentBearerToken(): String {
        return mockOAuth2Server.issueToken(
            issuerId = "issuer1",
            subject = "testbruker",
            audience = "dumbdumb",
            claims = mapOf(
                "oid" to "test-oid",
                "azp" to "test-azp",
                "NAVident" to "test123"
            )
        ).serialize()
    }

    @Test
    fun `hentKafkaConsumers returner informasjon om alle consumere`() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/admin/kafka/consumers")
                .header(API_KEY_HEADER, GYLDIG_API_NOKKEL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${hentBearerToken()}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val jsonResponse = result.response.contentAsString
        val kafkaConsumerResponses: List<KafkaConsumerResponse> = objectMapper.readValue(
            jsonResponse, object : TypeReference<List<KafkaConsumerResponse>>() {}
        )

        assertThat(kafkaConsumerResponses).hasSize(4)
    }

    @Test
    fun `stoppOgStartConsumer consumer stopped og startes`() {
        // Test stop
        val stopResult = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/admin/kafka/consumers/oppgaveHendelse/stop")
                .header(API_KEY_HEADER, GYLDIG_API_NOKKEL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${hentBearerToken()}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val stopJsonResponse = stopResult.response.contentAsString
        val kafkaConsumerResponseStop: KafkaConsumerResponse = objectMapper.readValue(
            stopJsonResponse, KafkaConsumerResponse::class.java
        )

        assertThat(kafkaConsumerResponseStop).isNotNull
        assertThat(kafkaConsumerResponseStop.active).isFalse

        // Test start
        val startResult = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/admin/kafka/consumers/oppgaveHendelse/start")
                .header(API_KEY_HEADER, GYLDIG_API_NOKKEL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${hentBearerToken()}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val startJsonResponse = startResult.response.contentAsString
        val kafkaConsumerResponseStart: KafkaConsumerResponse = objectMapper.readValue(
            startJsonResponse, KafkaConsumerResponse::class.java
        )

        assertThat(kafkaConsumerResponseStart).isNotNull
        assertThat(kafkaConsumerResponseStart.active).isTrue
    }

    @Test
    fun `settOffset sender inn offset2 consumer leser på nytt fra offset2`() {
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

        verify(oppgaveConsumer, timeout(1000).times(4)).hentOppgave(anyString())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/admin/kafka/consumers/oppgaveHendelse/seek/2")
                .header(API_KEY_HEADER, GYLDIG_API_NOKKEL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${hentBearerToken()}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        // Vi sender 4 meldinger, resetter til offset 2, melding med offset 2 og 3 leses på nytt, totalt hentes oppgave 6 ganger.
        verify(oppgaveConsumer, timeout(6_000).times(6)).hentOppgave(anyString())
    }

}
