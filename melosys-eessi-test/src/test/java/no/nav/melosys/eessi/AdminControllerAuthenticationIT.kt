package no.nav.melosys.eessi

import no.nav.security.mock.oauth2.MockOAuth2Server
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

/**
 * Tester for admin-kontroller autentisering som krever både API-nøkkel og bearer token.
 *
 * Disse testene verifiserer at alle admin-endepunkter krever korrekt autentisering
 * etter at de ble endret fra @Unprotected til @Protected.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
class AdminControllerAuthenticationIT : ComponentTestBase() {

    companion object {
        private const val API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY"
        private const val GYLDIG_API_NOKKEL = "dummy"
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mockOAuth2Server: MockOAuth2Server

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
}
