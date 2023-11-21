package no.nav.melosys.eessi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.service.journalfoering.OpprettUtgaaendeJournalpostService;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import no.nav.melosys.eessi.service.sed.SedService;
import no.nav.security.token.support.client.core.http.OAuth2HttpClient;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static no.nav.melosys.eessi.controller.ResponseBodyMatchers.responseBody;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SedController.class)
@ActiveProfiles("test")
class SedControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SedService sedService;

    @MockBean
    private OpprettUtgaaendeJournalpostService opprettUtgaaendeJournalpostService;

    @MockBean
    private TokenValidationContextHolder tokenValidationContextHolder;
    @MockBean
    private OAuth2HttpClient oAuth2HttpClient;

    @Test
    void genererPdfFraSed_manglerAdresse_ValidationException() throws Exception {
        SedDataDto sedDataDto = SedDataStub.getStub();
        sedDataDto.setBostedsadresse(null);
        sedDataDto.setKontaktadresse(null);
        sedDataDto.setOppholdsadresse(null);

        mockMvc.perform(post("/api/sed/{sedType}/pdf", SedType.A003)
                .content(objectMapper.writeValueAsString(sedDataDto)))
            .andExpect(status().isBadRequest())
            .andExpect(responseBody(objectMapper).containsError("message", "Personen mangler adresse ved PDF generering"))
            .andExpect(responseBody(objectMapper).containsError("error", "Bad Request"));

        verifyNoInteractions(sedService);
    }

    @Test
    void genererPdfFraSed_manglerAdresseOgSedKreverIkkeAdresse_ok() throws Exception {
        SedDataDto sedDataDto = SedDataStub.getStub();
        sedDataDto.setBostedsadresse(null);
        sedDataDto.setKontaktadresse(null);
        sedDataDto.setOppholdsadresse(null);

        mockMvc.perform(post("/api/sed/{sedType}/pdf", SedType.A005)
                .content(objectMapper.writeValueAsString(sedDataDto)))
            .andExpect(status().isOk());

        verify(sedService).genererPdfFraSed(sedDataDto, SedType.A005);
    }

    @Test
    void journalfoerTidligereSendteSed_existingSed_Success() throws Exception {
        String rinaSaksnummer = "12345";

        mockMvc.perform(post("/api/journalfoerTidligereSendteSedFor/{rinaSaksnummer}", rinaSaksnummer))
            .andExpect(status().isOk());

        verify(opprettUtgaaendeJournalpostService).journalfoerTidligereSedDersomEksisterer(rinaSaksnummer);
    }
}
