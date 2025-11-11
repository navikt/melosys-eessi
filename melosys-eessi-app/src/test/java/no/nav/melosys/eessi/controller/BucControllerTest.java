package no.nav.melosys.eessi.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.controller.dto.*;
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.SedVedlegg;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.buc.LukkBucService;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import no.nav.melosys.eessi.service.sed.SedService;
import no.nav.security.token.support.client.core.http.OAuth2HttpClient;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static no.nav.melosys.eessi.controller.ResponseBodyMatchers.responseBody;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BucController.class})
@ActiveProfiles("test")
class BucControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EuxService euxService;
    @MockitoBean
    private SedService sedService;
    @MockitoBean
    private LukkBucService lukkBucService;

    @MockitoBean
    private TokenValidationContextHolder tokenValidationContextHolder;
    @MockitoBean
    private OAuth2HttpClient oAuth2HttpClient;
    @MockitoBean
    private EuxConsumer euxConsumer;

    @Test
    void opprettBucOgSed_ok() throws Exception {
        OpprettBucOgSedDto opprettBucOgSedDto = lagOpprettBucOgSedDto(true);

        BucOgSedOpprettetDto bucOgSedOpprettetDto = BucOgSedOpprettetDto.builder()
            .rinaSaksnummer("123654")
            .rinaUrl("/rina/123654")
            .build();

        when(sedService.opprettBucOgSed(
            opprettBucOgSedDto.getSedDataDto(),
            opprettBucOgSedDto.getVedlegg(),
            BucType.LA_BUC_01,
            true,
            false)).thenReturn(bucOgSedOpprettetDto);

        mockMvc.perform(post("/api/buc/{bucType}", BucType.LA_BUC_01)
                .contentType(MediaType.APPLICATION_JSON)
                .param("sendAutomatisk", "true")
                .param("oppdaterEksisterende", "false")
                .content(objectMapper.writeValueAsString(opprettBucOgSedDto)))
            .andExpect(status().isOk())
            .andExpect(responseBody(objectMapper).containsObjectAsJson(bucOgSedOpprettetDto, BucOgSedOpprettetDto.class));
    }

    @Test
    void opprettBucOgSed_manglerAdresseOgSedKreverIkkeAdresse_ok() throws Exception {
        OpprettBucOgSedDto opprettBucOgSedDto = lagOpprettBucOgSedDto(false);

        BucOgSedOpprettetDto bucOgSedOpprettetDto = BucOgSedOpprettetDto.builder()
            .rinaSaksnummer("123654")
            .rinaUrl("/rina/123654")
            .build();

        when(sedService.opprettBucOgSed(
            opprettBucOgSedDto.getSedDataDto(),
            opprettBucOgSedDto.getVedlegg(),
            BucType.LA_BUC_03,
            true,
            false)).thenReturn(bucOgSedOpprettetDto);

        mockMvc.perform(post("/api/buc/{bucType}", BucType.LA_BUC_03)
                .contentType(MediaType.APPLICATION_JSON)
                .param("sendAutomatisk", "true")
                .param("oppdaterEksisterende", "false")
                .content(objectMapper.writeValueAsString(opprettBucOgSedDto)))
            .andExpect(status().isOk())
            .andExpect(responseBody(objectMapper).containsObjectAsJson(bucOgSedOpprettetDto, BucOgSedOpprettetDto.class));
    }

    @Test
    void opprettBucOgSed_manglerAdresse_ValidationException() throws Exception {
        OpprettBucOgSedDto opprettBucOgSedDto = lagOpprettBucOgSedDto(false);

        mockMvc.perform(post("/api/buc/{bucType}", BucType.LA_BUC_01)
                .contentType(MediaType.APPLICATION_JSON)
                .param("sendAutomatisk", "true")
                .param("oppdaterEksisterende", "false")
                .content(objectMapper.writeValueAsString(opprettBucOgSedDto)))
            .andExpect(status().isBadRequest())
            .andExpect(responseBody(objectMapper).containsError("message", "Personen mangler adresse"))
            .andExpect(responseBody(objectMapper).containsError("error", "Bad Request"));

    }

    @Test
    void sendPåEksisterendeBuc_manglerAdresse_ValidationException() throws Exception {
        SedDataDto sedDataDto = SedDataStub.getStub();
        sedDataDto.setBostedsadresse(null);
        sedDataDto.setOppholdsadresse(null);
        sedDataDto.setKontaktadresse(null);

        mockMvc.perform(post("/api/buc/{rinaSaksnummer}/sed/{sedType}", 1, SedType.A002)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sedDataDto)))
            .andExpect(status().isBadRequest())
            .andExpect(responseBody(objectMapper).containsError("message", "Personen mangler adresse"))
            .andExpect(responseBody(objectMapper).containsError("error", "Bad Request"));
    }

    @Test
    void sendPåEksisterendBuc_manglerAdresseOgSedKreverIkkeAdresse_ok() throws Exception {
        SedDataDto sedDataDto = SedDataStub.getStub();
        sedDataDto.setBostedsadresse(null);
        sedDataDto.setOppholdsadresse(null);
        sedDataDto.setKontaktadresse(null);

        mockMvc.perform(post("/api/buc/{rinaSaksnummer}/sed/{sedType}", 1, SedType.A005)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sedDataDto)))
            .andExpect(status().isOk());


        verify(sedService).sendPåEksisterendeBuc(sedDataDto, "1", SedType.A005);
    }

    @Test
    void sendPåEksisterendBuc_invaliderSed_ok() throws Exception {
        InvalideringSedDto invalideringSedDto = new InvalideringSedDto();
        invalideringSedDto.setUtstedelsedato(LocalDate.now().toString());
        invalideringSedDto.setSedTypeSomSkalInvalideres("A003");

        SedDataDto sedDataDto = SedDataStub.getStub();
        sedDataDto.setInvalideringSedDto(invalideringSedDto);
        sedDataDto.setBostedsadresse(new Adresse());

        mockMvc.perform(post("/api/buc/{rinaSaksnummer}/sed/{sedType}", 1, SedType.A003)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sedDataDto)))
            .andExpect(status().isOk());


        verify(sedService).sendPåEksisterendeBuc(sedDataDto, "1", SedType.A003);
    }

    @Test
    void hentSedGrunnlag_mapperForSedFinnesIkke_validationException() throws Exception {
        SED sed = new SED();
        sed.setSedType("A009");
        when(euxService.hentSed("23", "44")).thenReturn(sed);

        mockMvc.perform(get("/api/buc/{rinsSaksnummer}/sed/{rinaDokumentId}/grunnlag", 23, 44)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(responseBody(objectMapper).containsError("message", "Sed-type A009 støttes ikke"))
            .andExpect(responseBody(objectMapper).containsError("error", "Bad Request"));
    }

    @Test
    void hentMuligeBucHandlinger_euxConsumerReturnererListe_ok() throws Exception {
        when(euxConsumer.hentBucHandlinger("33")).thenReturn(List.of("CLOSE", "CREATE", "REOPEN"));

        mockMvc.perform(get("/api/buc/{rinaSaksnummer}/aksjoner", 33)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(responseBody(objectMapper).containsObjectAsJson(List.of("CLOSE", "CREATE", "REOPEN"), Collection.class));
    }

    @NotNull
    private OpprettBucOgSedDto lagOpprettBucOgSedDto(boolean medAdresser) throws IOException, URISyntaxException {
        OpprettBucOgSedDto opprettBucOgSedDto = new OpprettBucOgSedDto();
        SedDataDto sedDataDto = SedDataStub.getStub();
        if (!medAdresser) {
            sedDataDto.setBostedsadresse(null);
            sedDataDto.setKontaktadresse(null);
            sedDataDto.setOppholdsadresse(null);
        }
        opprettBucOgSedDto.setSedDataDto(sedDataDto);
        SedVedlegg sedVedlegg = new SedVedlegg("Søknad om medlemskap", "ny søknad om vedlegg".getBytes());
        opprettBucOgSedDto.setVedlegg(List.of(sedVedlegg));
        return opprettBucOgSedDto;
    }
}
