package no.nav.melosys.eessi.service.kontroll;

import java.time.LocalDateTime;

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
    void hentFeiledeSeder_enFeiledSedMottat_viserFeilmeldingSisteHendelse() {
        when(sedMottattHendelseRepository.findAllByJournalpostIdNullSortedByMottattDato())
            .thenReturn(singletonList(sedMottattHendelse));

        var response = sedMottakAdminTjeneste.hentSEDerMottattUtenJournalpostId(apiKey);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var body = response.getBody();
        assertThat(body)
            .flatExtracting(SedMottattHendelseDto::getId, SedMottattHendelseDto::getMottattDato, SedMottattHendelseDto::getJournalpostId)
            .containsExactly(sedMottattHendelse.getId(), sedMottattHendelse.getMottattDato(), sedMottattHendelse.getJournalpostId());

        assertThat(body.stream().findFirst().isPresent()).isTrue();
        assertThat(body.stream().findFirst().get().getJournalpostId()).isNull();
    }

    @Test
    void restartAlleSEDerUtenJournalpostId_() {
        final ArgumentCaptor<SedMottattHendelse> valueCapture = ArgumentCaptor.forClass(SedMottattHendelse.class);

        when(sedMottattHendelseRepository.findAllByJournalpostIdNullSortedByMottattDato())
            .thenReturn(singletonList(sedMottattHendelse));
        doNothing().when(sedMottakService).behandleSed(valueCapture.capture());

        sedMottakAdminTjeneste.restartAlleSEDerUtenJournalpostId(apiKey);

        assertThat(valueCapture.getValue()).isEqualTo(sedMottattHendelse);
        verify(sedMottakService, times(1)).behandleSed(any(SedMottattHendelse.class));

    }


    private SedMottattHendelse lagFeiledSedMottakHendelse() {
        return lagFeiledSedMottakHendelse(LocalDateTime.now());
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
