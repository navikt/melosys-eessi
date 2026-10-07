package no.nav.melosys.eessi

import no.nav.melosys.eessi.controller.interceptor.AdminTilgangInterceptor
import no.nav.security.mock.oauth2.MockOAuth2Server
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

/**
 * Tester for admin-kontroller autentisering som krever både API-nøkkel og bearer token,
 * og driftsgruppe for personkall.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
class AdminControllerAuthenticationIT : ComponentTestBase() {

    companion object {
        private const val API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY"
        private const val GYLDIG_API_NOKKEL = "dummy"
        private const val ANNEN_GRUPPE = "00000000-0000-0000-0000-000000000002"
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mockOAuth2Server: MockOAuth2Server

    @Value("\${melosys.admin.driftsgruppe}")
    private lateinit var driftsgruppeId: String

    private fun hentBearerToken(): String = personToken(grupper = listOf(driftsgruppeId))

    private fun personToken(grupper: List<String>?): String =
        token(buildMap {
            put("NAVident", "test123")
            grupper?.let { put("groups", it) }
        })

    private fun maskinToken(): String = token(mapOf("idtyp" to "app"))

    private fun token(claims: Map<String, Any>): String =
        mockOAuth2Server.issueToken(
            issuerId = "issuer1",
            subject = "testbruker",
            audience = "dumbdumb",
            claims = mapOf("oid" to "test-oid", "azp" to "test-azp") + claims
        ).serialize()

    private fun hent(sti: String, token: String?, nøkkel: String? = GYLDIG_API_NOKKEL): ResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.get(sti)
                .apply {
                    token?.let { header("Authorization", "Bearer $it") }
                    nøkkel?.let { header(API_KEY_HEADER, it) }
                }
                .accept(MediaType.APPLICATION_JSON)
        )

    @Test
    fun `personkall med driftsgruppe og nøkkel får tilgang`() {
        hent("/api/admin/kafka/dlq", personToken(grupper = listOf(driftsgruppeId)))
            .andExpect(status().isOk)
    }

    @Test
    fun `personkall uten driftsgruppe avvises med forklaring, selv med riktig nøkkel`() {
        hent("/api/admin/kafka/dlq", personToken(grupper = listOf(ANNEN_GRUPPE)))
            .andExpect(status().isForbidden)
            .andExpect(content().string(AdminTilgangInterceptor.MANGLER_DRIFTSGRUPPE))
    }

    @Test
    fun `personkall uten groups-claim avvises`() {
        hent("/api/admin/kafka/dlq", personToken(grupper = null))
            .andExpect(status().isForbidden)
            .andExpect(content().string(AdminTilgangInterceptor.MANGLER_DRIFTSGRUPPE))
    }

    @Test
    fun `token med annen idtyp enn app regnes som personkall`() {
        hent("/api/admin/kafka/dlq", token(mapOf("idtyp" to "user")))
            .andExpect(status().isForbidden)
            .andExpect(content().string(AdminTilgangInterceptor.MANGLER_DRIFTSGRUPPE))
    }

    @Test
    fun `gruppesjekken gjelder også adminruter uten api-prefiks`() {
        hent("/admin/sedmottak/feilede", personToken(grupper = listOf(ANNEN_GRUPPE)))
            .andExpect(status().isForbidden)
            .andExpect(content().string(AdminTilgangInterceptor.MANGLER_DRIFTSGRUPPE))
    }

    @Test
    fun `maskinkall slipper gjennom gruppesjekken`() {
        hent("/admin/sedmottak/feilede", maskinToken())
            .andExpect(status().isOk)
    }

    @Test
    fun `nøkkelen kreves fortsatt for maskinkall`() {
        hent("/admin/sedmottak/feilede", maskinToken(), nøkkel = null)
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.message", containsString(API_KEY_HEADER)))
    }

    @Test
    fun `nøkkelen kreves fortsatt for personkall med driftsgruppe`() {
        hent("/api/admin/kafka/dlq", personToken(grupper = listOf(driftsgruppeId)), nøkkel = "feil-api-nokkel")
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.message").value("Ugyldig API-nøkkel"))
    }

    @Test
    fun `sed-mottatt-lager er ikke omfattet av gruppesjekken`() {
        hent("/api/admin/sed-mottatt-lager", personToken(grupper = listOf(ANNEN_GRUPPE)), nøkkel = null)
            .andExpect(status().isOk)
    }

    @Test
    fun `skal kreve både API-nøkkel og bearer token for alle admin endepunkter`() {
        val endepunkter = listOf(
            "/api/admin/kafka/consumers",
            "/api/admin/kafka/dlq"
        )

        endepunkter.forEach { endepunkt ->
            // Test manglende API-nøkkel og bearer token
            mockMvc.perform(
                MockMvcRequestBuilders.get(endepunkt)
                    .accept(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError)

            // Test feil API-nøkkel
            mockMvc.perform(
                MockMvcRequestBuilders.get(endepunkt)
                    .header(API_KEY_HEADER, "feil-api-nokkel")
                    .accept(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError)

            // Test manglende bearer token (kun API-nøkkel)
            mockMvc.perform(
                MockMvcRequestBuilders.get(endepunkt)
                    .header(API_KEY_HEADER, GYLDIG_API_NOKKEL)
                    .accept(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError)

            // Test både API-nøkkel og bearer token korrekte
            mockMvc.perform(
                MockMvcRequestBuilders.get(endepunkt)
                    .header(API_KEY_HEADER, GYLDIG_API_NOKKEL)
                    .header("Authorization", "Bearer ${hentBearerToken()}")
                    .accept(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
        }
    }

    @Test
    fun `skal kreve autentisering for POST operasjoner på admin endepunkter`() {
        // Test POST operasjoner på KafkaAdminTjeneste
        listOf("oppgaveHendelse", "sedMottatt").forEach { consumerId ->
            // Stop consumer - uten autentisering
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/admin/kafka/consumers/$consumerId/stop")
                    .accept(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError)

            // Start consumer - uten autentisering
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/admin/kafka/consumers/$consumerId/start")
                    .accept(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError)

            // Stop consumer - med korrekt autentisering
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/admin/kafka/consumers/$consumerId/stop")
                    .header(API_KEY_HEADER, GYLDIG_API_NOKKEL)
                    .header("Authorization", "Bearer ${hentBearerToken()}")
                    .accept(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isOk)

            // Start consumer - med korrekt autentisering
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/admin/kafka/consumers/$consumerId/start")
                    .header(API_KEY_HEADER, GYLDIG_API_NOKKEL)
                    .header("Authorization", "Bearer ${hentBearerToken()}")
                    .accept(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
        }

        // Test POST operasjoner på KafkaDLQAdminTjeneste
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/admin/kafka/dlq/restart/alle")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isInternalServerError)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/admin/kafka/dlq/restart/alle")
                .header(API_KEY_HEADER, GYLDIG_API_NOKKEL)
                .header("Authorization", "Bearer ${hentBearerToken()}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `skal kreve autentisering for DELETE på dlq endepunkt`() {
        val uuid = UUID.randomUUID()

        // Uten autentisering
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/admin/kafka/dlq/$uuid")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isInternalServerError)

        // Feil API-nøkkel
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/admin/kafka/dlq/$uuid")
                .header(API_KEY_HEADER, "feil-api-nokkel")
                .header("Authorization", "Bearer ${hentBearerToken()}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }
}
