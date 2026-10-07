package no.nav.melosys.eessi

import com.nimbusds.oauth2.sdk.TokenRequest
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.controller.interceptor.AdminTilgangInterceptor
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.mock.oauth2.token.OAuth2TokenCallback
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.util.UUID

/**
 * Tilgang til adminrutene: gyldig Azure-token fra Console, og driftsgruppe for personkall.
 * Adminnøkkelen er fjernet, og nøkkelheaderen ignoreres.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
class AdminControllerAuthenticationIT : ComponentTestBase() {

    companion object {
        private const val API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY"
        private const val ANNEN_GRUPPE = "00000000-0000-0000-0000-000000000002"
        private const val ANNEN_KLIENT = "annen-klient-id"
        // Unntatt fra adminsjekken i ApiConfig
        private const val SED_MOTTATT_LAGER = "/api/admin/sed-mottatt-lager"
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private lateinit var handlerMapping: RequestMappingHandlerMapping

    @Autowired
    private lateinit var mockOAuth2Server: MockOAuth2Server

    @Value("\${melosys.admin.driftsgruppe}")
    private lateinit var driftsgruppeId: String

    @Value("\${melosys.admin.console-klient-id}")
    private lateinit var consoleKlientId: String

    private fun personToken(
        grupper: List<String>? = listOf(driftsgruppeId),
        azp: String? = consoleKlientId,
        ekstraClaims: Map<String, Any> = emptyMap(),
    ): String = token(
        azp = azp,
        claims = buildMap {
            put("NAVident", "test123")
            grupper?.let { put("groups", it) }
            putAll(ekstraClaims)
        }
    )

    private fun maskinToken(azp: String = consoleKlientId): String =
        token(azp = azp, claims = mapOf("idtyp" to "app"))

    // mock-oauth2-server kan overskrive azp med klient-ID-en, så den settes begge steder.
    private fun token(azp: String?, claims: Map<String, Any>, issuerId: String = "issuer1", audience: String = "dumbdumb"): String {
        val callback = DefaultOAuth2TokenCallback(
            issuerId = issuerId,
            subject = "testbruker",
            audience = listOf(audience),
            claims = mapOf("oid" to "test-oid") + claims + (azp?.let { mapOf("azp" to it) } ?: emptyMap())
        )
        val callbackUtenAzp = object : OAuth2TokenCallback by callback {
            override fun addClaims(tokenRequest: TokenRequest): Map<String, Any> = callback.addClaims(tokenRequest) - "azp"
        }
        return mockOAuth2Server.issueToken(issuerId, azp ?: "ubrukt", if (azp == null) callbackUtenAzp else callback).serialize()
    }

    private fun stsToken(): String =
        token(azp = null, claims = emptyMap(), issuerId = "reststs", audience = "srvmelosys")

    private fun kall(forespørsel: MockHttpServletRequestBuilder, token: String?, nøkkel: String? = null): ResultActions =
        mockMvc.perform(
            forespørsel
                .apply {
                    token?.let { header("Authorization", "Bearer $it") }
                    nøkkel?.let { header(API_KEY_HEADER, it) }
                }
                .accept(MediaType.APPLICATION_JSON)
        )

    private fun hent(sti: String, token: String?, nøkkel: String? = null) = kall(get(sti), token, nøkkel)

    private fun ResultActions.avvistMed(forventetStatus: Int, melding: String) =
        andExpect(status().`is`(forventetStatus)).andExpect(content().string(melding))

    // Uten gyldig Azure-token

    @Test
    fun `kall uten token avvises med 401`() {
        hent("/api/admin/kafka/dlq", token = null)
            .avvistMed(401, AdminTilgangInterceptor.MANGLER_AZURE_TOKEN)
    }

    @Test
    fun `kall med ugyldig token avvises med 401`() {
        hent("/api/admin/kafka/dlq", token = "ugyldig-token")
            .avvistMed(401, AdminTilgangInterceptor.MANGLER_AZURE_TOKEN)
    }

    @Test
    fun `gyldig STS-token avvises med 401, også med den gamle nøkkelen`() {
        hent("/api/admin/kafka/dlq", stsToken(), nøkkel = "dummy")
            .avvistMed(401, AdminTilgangInterceptor.MANGLER_AZURE_TOKEN)
    }

    @Test
    fun `STS-token avvises også på adminruter uten api-prefiks`() {
        hent("/admin/sedmottak/feilede", stsToken())
            .avvistMed(401, AdminTilgangInterceptor.MANGLER_AZURE_TOKEN)
    }

    // Klient

    @Test
    fun `personkall fra annen klient avvises, selv med driftsgruppe`() {
        hent("/api/admin/kafka/dlq", personToken(azp = ANNEN_KLIENT))
            .avvistMed(403, AdminTilgangInterceptor.UKJENT_KLIENT)
    }

    @Test
    fun `personkall uten azp avvises`() {
        hent("/api/admin/kafka/dlq", personToken(azp = null))
            .avvistMed(403, AdminTilgangInterceptor.UKJENT_KLIENT)
    }

    @Test
    fun `maskinkall fra annen klient avvises`() {
        hent("/admin/sedmottak/feilede", maskinToken(azp = ANNEN_KLIENT))
            .avvistMed(403, AdminTilgangInterceptor.UKJENT_KLIENT)
    }

    // Personkall fra Console

    @Test
    fun `personkall fra Console med driftsgruppe får tilgang uten nøkkel`() {
        hent("/api/admin/kafka/dlq", personToken())
            .andExpect(status().isOk)
    }

    @Test
    fun `nøkkelheaderen ignoreres`() {
        hent("/api/admin/kafka/dlq", personToken(), nøkkel = "feil-api-nokkel")
            .andExpect(status().isOk)
    }

    @Test
    fun `personkall uten driftsgruppe avvises med forklaring`() {
        hent("/api/admin/kafka/dlq", personToken(grupper = listOf(ANNEN_GRUPPE)))
            .avvistMed(403, AdminTilgangInterceptor.MANGLER_DRIFTSGRUPPE)
    }

    @Test
    fun `personkall uten groups-claim avvises`() {
        hent("/api/admin/kafka/dlq", personToken(grupper = null))
            .avvistMed(403, AdminTilgangInterceptor.MANGLER_DRIFTSGRUPPE)
    }

    @Test
    fun `token med annen idtyp enn app regnes som personkall`() {
        hent("/api/admin/kafka/dlq", personToken(grupper = null, ekstraClaims = mapOf("idtyp" to "user")))
            .avvistMed(403, AdminTilgangInterceptor.MANGLER_DRIFTSGRUPPE)
    }

    @Test
    fun `gruppesjekken gjelder også adminruter uten api-prefiks`() {
        hent("/admin/sedmottak/feilede", personToken(grupper = listOf(ANNEN_GRUPPE)))
            .avvistMed(403, AdminTilgangInterceptor.MANGLER_DRIFTSGRUPPE)
    }

    // Maskinkall fra Console

    @Test
    fun `maskinkall fra Console får tilgang uten nøkkel`() {
        hent("/admin/sedmottak/feilede", maskinToken())
            .andExpect(status().isOk)
    }

    @Test
    fun `maskinkall fra Console får tilgang også utenfor de automatiske rutene`() {
        hent("/api/admin/kafka/dlq", maskinToken())
            .andExpect(status().isOk)
    }

    // Utenfor adminsjekken

    @Test
    fun `sed-mottatt-lager er ikke omfattet av adminsjekken`() {
        hent("/api/admin/sed-mottatt-lager", personToken(grupper = listOf(ANNEN_GRUPPE), azp = ANNEN_KLIENT))
            .andExpect(status().isOk)
    }

    // Skrivende metoder

    @Test
    fun `POST og DELETE krever tilgang og virker uten nøkkel`() {
        listOf("oppgaveHendelse", "sedMottatt").forEach { consumerId ->
            kall(post("/api/admin/kafka/consumers/$consumerId/stop"), token = null)
                .avvistMed(401, AdminTilgangInterceptor.MANGLER_AZURE_TOKEN)
            kall(post("/api/admin/kafka/consumers/$consumerId/stop"), personToken())
                .andExpect(status().isOk)
            kall(post("/api/admin/kafka/consumers/$consumerId/start"), personToken())
                .andExpect(status().isOk)
        }

        kall(post("/api/admin/kafka/dlq/restart/alle"), personToken(grupper = listOf(ANNEN_GRUPPE)))
            .avvistMed(403, AdminTilgangInterceptor.MANGLER_DRIFTSGRUPPE)
        kall(post("/api/admin/kafka/dlq/restart/alle"), personToken())
            .andExpect(status().isOk)

        kall(delete("/api/admin/kafka/dlq/${UUID.randomUUID()}"), token = null)
            .avvistMed(401, AdminTilgangInterceptor.MANGLER_AZURE_TOKEN)
    }

    // Alle registrerte adminruter
    //
    // Rutene hentes fra Spring, så nye adminkontrollere dekkes uten at testene må oppdateres.
    // assertSoftly viser alle ruter som feiler, ikke bare den første.

    @Test
    fun `kall uten token avvises på alle registrerte adminruter`() {
        val endepunkter = registrerteAdminEndepunkter()

        assertSoftly {
            endepunkter.forEach { endepunkt ->
                withClue(endepunkt) {
                    val respons = kall(endepunkt, token = null)
                    respons.status shouldBe 401
                    respons.contentAsString shouldBe AdminTilgangInterceptor.MANGLER_AZURE_TOKEN
                }
            }
        }
    }

    @Test
    fun `kall fra annen klient avvises på alle registrerte adminruter`() {
        val endepunkter = registrerteAdminEndepunkter()
        val token = personToken(azp = ANNEN_KLIENT)

        assertSoftly {
            endepunkter.forEach { endepunkt ->
                withClue(endepunkt) {
                    val respons = kall(endepunkt, token)
                    respons.status shouldBe 403
                    respons.contentAsString shouldBe AdminTilgangInterceptor.UKJENT_KLIENT
                }
            }
        }
    }

    @Test
    fun `personkall uten driftsgruppe avvises på alle registrerte adminruter`() {
        val endepunkter = registrerteAdminEndepunkter()
        val token = personToken(grupper = listOf(ANNEN_GRUPPE))

        assertSoftly {
            endepunkter.forEach { endepunkt ->
                withClue(endepunkt) {
                    val respons = kall(endepunkt, token)
                    respons.status shouldBe 403
                    respons.contentAsString shouldBe AdminTilgangInterceptor.MANGLER_DRIFTSGRUPPE
                }
            }
        }
    }

    private data class Endepunkt(val metode: HttpMethod, val mønster: String) {
        // Interceptoren avviser før argumentene leses, så stivariablene trenger bare å matche mønsteret
        val sti = mønster.replace(Regex("\\{[^}]+}"), "1")

        override fun toString() = "$metode $mønster"
    }

    private fun registrerteAdminEndepunkter(): List<Endepunkt> {
        val endepunkter = handlerMapping.handlerMethods.keys.flatMap { info ->
            val metoder = info.methodsCondition.methods.ifEmpty { setOf(RequestMethod.GET) }
            info.patternValues
                .filter { erAdminrute(it) && !it.startsWith(SED_MOTTATT_LAGER) }
                .flatMap { mønster -> metoder.map { Endepunkt(it.asHttpMethod(), mønster) } }
        }

        // Vakt mot falsk grønn: finner oppslaget ingen ruter, kjører forEach ingen assertions, og testene
        // passerer uten å ha sjekket noe. Den ene ruten har /api-prefiks, den andre ligger utenfor
        // controller-pakken og har det ikke, så vakten viser at begge variantene blir funnet.
        endepunkter.map { it.toString() }.shouldContainAll(
            "GET /api/admin/kafka/dlq",
            "GET /admin/sedmottak/feilede",
        )
        return endepunkter
    }

    // Samme ruter som AdminTilgangInterceptor er registrert for i ApiConfig
    private fun erAdminrute(mønster: String) = mønster.startsWith("/admin/") || mønster.startsWith("/api/admin/")

    private fun kall(endepunkt: Endepunkt, token: String?): MockHttpServletResponse =
        kall(request(endepunkt.metode, endepunkt.sti).contentType(MediaType.APPLICATION_JSON), token)
            .andReturn().response
}
