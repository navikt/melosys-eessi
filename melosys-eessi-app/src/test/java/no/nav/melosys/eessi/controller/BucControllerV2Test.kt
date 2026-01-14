package no.nav.melosys.eessi.controller

import no.nav.melosys.eessi.controller.ResponseBodyMatchers.responseBody
import no.nav.melosys.eessi.controller.dto.*
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.service.sed.SedDataStub
import no.nav.melosys.eessi.service.sed.SedService
import no.nav.security.token.support.client.core.http.OAuth2HttpClient
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.json.JsonMapper

@WebMvcTest(controllers = [BucControllerV2::class])
@ActiveProfiles("test")
class BucControllerV2Test {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jsonMapper: JsonMapper

    @MockitoBean
    private lateinit var sedService: SedService

    @MockitoBean
    private lateinit var tokenValidationContextHolder: TokenValidationContextHolder

    @MockitoBean
    private lateinit var oAuth2HttpClient: OAuth2HttpClient

    @Test
    fun `opprettBucOgSed - should create buc and sed successfully`() {
        val request = lagOpprettBucOgSedDtoV2(medAdresser = true)

        val bucOgSedOpprettetDto = BucOgSedOpprettetDto.builder()
            .rinaSaksnummer("123654")
            .rinaUrl("/rina/123654")
            .build()

        `when`(sedService.opprettBucOgSed(request)).thenReturn(bucOgSedOpprettetDto)

        mockMvc.perform(
            post("/api/v2/buc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(responseBody(jsonMapper).containsObjectAsJson(bucOgSedOpprettetDto, BucOgSedOpprettetDto::class.java))
    }

    @Test
    fun `opprettBucOgSed - should succeed when address not required`() {
        val request = lagOpprettBucOgSedDtoV2(medAdresser = false, bucType = BucType.LA_BUC_03)

        val bucOgSedOpprettetDto = BucOgSedOpprettetDto.builder()
            .rinaSaksnummer("123654")
            .rinaUrl("/rina/123654")
            .build()

        `when`(sedService.opprettBucOgSed(request)).thenReturn(bucOgSedOpprettetDto)

        mockMvc.perform(
            post("/api/v2/buc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(responseBody(jsonMapper).containsObjectAsJson(bucOgSedOpprettetDto, BucOgSedOpprettetDto::class.java))
    }

    @Test
    fun `opprettBucOgSed - should fail when address is missing and required`() {
        val request = lagOpprettBucOgSedDtoV2(medAdresser = false, bucType = BucType.LA_BUC_01)

        mockMvc.perform(
            post("/api/v2/buc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(responseBody(jsonMapper).containsError("message", "Personen mangler adresse"))
            .andExpect(responseBody(jsonMapper).containsError("error", "Bad Request"))
    }

    @Test
    fun `opprettBucOgSed - should fail when sedDataDto is null`() {
        val request = """
            {
                "bucType": "LA_BUC_01",
                "sedDataDto": null,
                "sendAutomatisk": true,
                "oppdaterEksisterende": false,
                "vedlegg": []
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v2/buc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `opprettBucOgSed - should fail when bucType is null`() {
        val request = """
            {
                "bucType": null,
                "sedDataDto": {},
                "sendAutomatisk": true,
                "oppdaterEksisterende": false,
                "vedlegg": []
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v2/buc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        )
            .andExpect(status().isBadRequest)
    }

    private fun lagOpprettBucOgSedDtoV2(
        medAdresser: Boolean,
        bucType: BucType = BucType.LA_BUC_01
    ): OpprettBucOgSedDtoV2 {
        val sedDataDto = SedDataStub.getStub()
        if (!medAdresser) {
            sedDataDto.bostedsadresse = null
            sedDataDto.kontaktadresse = null
            sedDataDto.oppholdsadresse = null
        }

        val vedlegg = listOf(
            VedleggReferanse(
                journalpostId = "12345",
                dokumentId = "67890",
                tittel = "Søknad om medlemskap"
            )
        )

        return OpprettBucOgSedDtoV2(
            bucType = bucType,
            sedDataDto = sedDataDto,
            vedlegg = vedlegg,
            sendAutomatisk = true,
            oppdaterEksisterende = false
        )
    }
}
