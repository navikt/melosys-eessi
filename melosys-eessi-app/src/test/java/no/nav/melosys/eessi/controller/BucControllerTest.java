package no.nav.melosys.eessi.controller;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.controller.dto.BucOgSedOpprettetDto;
import no.nav.melosys.eessi.controller.dto.OpprettBucOgSedDto;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.SedVedlegg;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.buc.LukkBucService;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import no.nav.melosys.eessi.service.sed.SedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static no.nav.melosys.eessi.controller.ResponseBodyMatchers.responseBody;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BucController.class})
class BucControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    @Qualifier("tokenContext")
    private EuxService euxService;
    @MockBean
    private SedService sedService;
    @MockBean
    private LukkBucService lukkBucService;

    @Test
    void opprettBucOgSed_happy() throws Exception {
        OpprettBucOgSedDto opprettBucOgSedDto = new OpprettBucOgSedDto();
        opprettBucOgSedDto.setSedDataDto(SedDataStub.getStub());
        SedVedlegg sedVedlegg = new SedVedlegg();
        sedVedlegg.setTittel("Søknad om medlemskap");
        sedVedlegg.setInnhold("ny søknad om vedlegg".getBytes());
        opprettBucOgSedDto.setVedlegg(List.of(sedVedlegg));

        BucOgSedOpprettetDto bucOgSedOpprettetDto = BucOgSedOpprettetDto.builder()
            .rinaSaksnummer("123654")
            .rinaUrl("/rina/123654")
            .build();

        when(sedService.opprettBucOgSed(
            eq(opprettBucOgSedDto.getSedDataDto()),
            eq(opprettBucOgSedDto.getVedlegg()),
            eq(BucType.LA_BUC_01),
            eq(true),
            eq(false))).thenReturn(bucOgSedOpprettetDto);

        mockMvc.perform(post("/api/buc/{bucType}", BucType.LA_BUC_01)
                .contentType(MediaType.APPLICATION_JSON)
                .param("sendAutomatisk", "true")
                .param("oppdaterEksisterende", "false")
                .content(objectMapper.writeValueAsString(opprettBucOgSedDto)))
            .andExpect(status().isOk())
            .andExpect(responseBody(objectMapper).containsObjectAsJson(bucOgSedOpprettetDto, BucOgSedOpprettetDto.class));

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
            .andExpect(responseBody(objectMapper).containsError("message", "Personen mangler adresse - rinaSaksnummer=1 og sedType=A002"))
            .andExpect(responseBody(objectMapper).containsError("error", "Bad Request"));
    }

    @Test
    void sendPåEksisterendBuc_manglerAdresse_ok() throws Exception {
        SedDataDto sedDataDto = SedDataStub.getStub();
        sedDataDto.setBostedsadresse(null);
        sedDataDto.setOppholdsadresse(null);
        sedDataDto.setKontaktadresse(null);

        mockMvc.perform(post("/api/buc/{rinaSaksnummer}/sed/{sedType}", 1, SedType.A005)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sedDataDto)))
            .andExpect(status().isOk());


        verify(sedService, times(1)).sendPåEksisterendeBuc(eq(sedDataDto), eq("1"), eq(SedType.A005));
    }

    @Test
    void hentSedGrunnlag_mapperForSedFinnesIkke_validationException() throws Exception {
        SED sed = new SED();
        sed.setSedType("A009");
        when(euxService.hentSed(eq("23"), eq("44"))).thenReturn(sed);

        mockMvc.perform(get("/api/buc/{rinsSaksnummer}/sed/{rinaDokumentId}/grunnlag", 23, 44)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(responseBody(objectMapper).containsError("message", "Sed-type A009 støttes ikke"))
            .andExpect(responseBody(objectMapper).containsError("error", "Bad Request"));
    }
}
