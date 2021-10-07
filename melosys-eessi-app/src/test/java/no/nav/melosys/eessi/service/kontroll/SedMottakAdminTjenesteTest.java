package no.nav.melosys.eessi.service.kontroll;

import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.models.SedMottattHendelseDto;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import no.nav.melosys.eessi.service.mottak.SedMottakService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SedMottakAdminTjenesteTest {
    @Mock
    private SedMottattHendelseRepository sedMottattHendelseRepository;
    @Mock
    private SedMottakService sedMottakService;

    private final String apiKey = "dummy";
    private SedMottakAdminTjeneste sedMottakAdminTjeneste;
    private SedMottattHendelse sedMottattHendelse;


    @BeforeEach
    void setUp() {
        sedMottakAdminTjeneste = new SedMottakAdminTjeneste(sedMottakService, sedMottattHendelseRepository, apiKey);
        sedMottattHendelse = lagFeiledSedMottakHendelse();
    }

    @Test
    void hentFeiledeSeder_enFeiledSedMottat_viserFeilmeldingSisteHendelse() throws JsonProcessingException {
        when(sedMottattHendelseRepository.findAllByJournalpostIdIsNullOrderByMottattDato())
            .thenReturn(singletonList(sedMottattHendelse));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        var response = sedMottakAdminTjeneste.hentSEDerMottattUtenJournalpostId(apiKey);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var body = response.getBody();
        assert body != null;
        assertThat(body)
            .flatExtracting(SedMottattHendelseDto::id, SedMottattHendelseDto::mottattDato, SedMottattHendelseDto::journalpostId)
            .containsExactly(sedMottattHendelse.getId(), sedMottattHendelse.getMottattDato(), sedMottattHendelse.getJournalpostId());

        assertThat(body.stream().findFirst().isPresent()).isTrue();
        assertThat(body.stream().findFirst().get().journalpostId()).isNull();

        String serializedobject = mapper.writeValueAsString(body.stream().findFirst().get());
        assertThat(serializedobject).isEqualTo("{\"id\":1,\"sedHendelse\":null,\"journalpostId\":null,\"publisertKafka\":false,\"mottattDato\":\"2021-01-01T00:00:00\",\"sistEndretDato\":null}");
    }

    @Test
    void restartAlleSEDerUtenJournalpostId_() {
        final ArgumentCaptor<SedMottattHendelse> valueCapture = ArgumentCaptor.forClass(SedMottattHendelse.class);

        when(sedMottattHendelseRepository.findAllByJournalpostIdIsNullOrderByMottattDato())
            .thenReturn(singletonList(sedMottattHendelse));
        doNothing().when(sedMottakService).behandleSed(valueCapture.capture());

        sedMottakAdminTjeneste.restartAlleSEDerUtenJournalpostId(apiKey);

        assertThat(valueCapture.getValue()).isEqualTo(sedMottattHendelse);
        verify(sedMottakService, times(1)).behandleSed(any(SedMottattHendelse.class));

    }


    private SedMottattHendelse lagFeiledSedMottakHendelse() {
        return lagFeiledSedMottakHendelse(LocalDateTime.of(2021, 1, 1, 0, 0));
    }

    private SedMottattHendelse lagFeiledSedMottakHendelse(LocalDateTime registrertDato) {
        SedMottattHendelse sedMottattHendelse = new SedMottattHendelse();
        sedMottattHendelse.setJournalpostId(null);
        sedMottattHendelse.setMottattDato(registrertDato);
        sedMottattHendelse.setId(1L);
        sedMottattHendelse.setPublisertKafka(false);
        return sedMottattHendelse;
    }
}
