package no.nav.melosys.eessi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import no.nav.melosys.eessi.service.sed.SedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static no.nav.melosys.eessi.controller.ResponseBodyMatchers.responseBody;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SedController.class)
class SedControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SedService sedService;

    @Test
    void genererPdfFraSed_manglerAdresse_ValidationException() throws Exception {
        SedDataDto sedDataDto = SedDataStub.getStub();
        sedDataDto.setBostedsadresse(null);
        sedDataDto.setKontaktadresse(null);
        sedDataDto.setOppholdsadresse(null);

        mockMvc.perform(post("/api/sed/{sedType}/pdf", SedType.A003)
                .content(objectMapper.writeValueAsString(sedDataDto)))
            .andExpect(status().isBadRequest())
            .andExpect(responseBody(objectMapper).containsError("message", "Personen mangler adresse ved PDF generering fra sedType=A003"))
            .andExpect(responseBody(objectMapper).containsError("error", "Bad Request"));

        verifyNoInteractions(sedService);
    }

    @Test
    void genererPdfFraSed_manglerAdresse_ok() throws Exception {
        SedDataDto sedDataDto = SedDataStub.getStub();
        sedDataDto.setBostedsadresse(null);
        sedDataDto.setKontaktadresse(null);
        sedDataDto.setOppholdsadresse(null);

        mockMvc.perform(post("/api/sed/{sedType}/pdf", SedType.A005)
                .content(objectMapper.writeValueAsString(sedDataDto)))
            .andExpect(status().isOk());

        verify(sedService, times(1)).genererPdfFraSed(eq(sedDataDto), eq(SedType.A005));
    }
}
