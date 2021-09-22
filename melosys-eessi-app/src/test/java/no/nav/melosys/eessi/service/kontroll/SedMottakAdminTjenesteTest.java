package no.nav.melosys.eessi.service.kontroll;

import java.time.LocalDateTime;

import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import no.nav.melosys.eessi.service.mottak.SedMottakService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SedMottakAdminTjenesteTest {
    @Mock
    private SedMottattHendelseRepository sedMottattHendelseRepository;
    @Mock
    private SedMottakService sedMottakService;

    private final String apiKey = "dummy";
    private SedMottakAdminTjeneste sedMottakAdminTjeneste;

    @BeforeEach
    void setUp() {
        sedMottakAdminTjeneste = new SedMottakAdminTjeneste(sedMottakService, sedMottattHendelseRepository, apiKey);
    }

    @Test
    void hentSederMottattUtenJournalpostId() {
    }

    @Test
    void hentFeiledeSeder_enFeiledSedMottat_viserFeilmeldingSisteHendelse() {
        final var sisteFeilmelding = "siste feilmelding";
        SedMottattHendelse sedMottattHendelse = lagFeiledSedMottakHendelse();

        when(sedMottattHendelseRepository.findAll())
            .thenReturn(singletonList(sedMottattHendelse));

        var response = sedMottakAdminTjeneste.hentSederMottattUtenJournalpostId(apiKey);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var body = response.getBody();
        assertThat(body)
            .flatExtracting(SedMottattHendelse::getId, SedMottattHendelse::getMottattDato, SedMottattHendelse::getJournalpostId)
            .containsExactly(sedMottattHendelse.getId(), sedMottattHendelse.getMottattDato(), sedMottattHendelse.getJournalpostId());

        assertThat(response.getBody().stream().findFirst().isPresent()).isTrue();
        assertThat(response.getBody().stream().findFirst().get().getJournalpostId()).isNull();
    }

    @Test
    void restartAlleFeiledeProsessinstanser() {

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

    private SedHendelse lagSedHendelse() {
        var sedHendelse = new SedHendelse();
        sedHendelse.setId(1L);
        return sedHendelse;
    }
}
