package no.nav.melosys.eessi.jobs;

import java.util.Arrays;

import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.SedMottatt;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.behandling.BehandleSedMottattService;
import no.nav.melosys.eessi.service.sed.SedMottattService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SedMottattJobbTest {

    @Mock
    private SedMottattService sedMottattService;
    @Mock
    private BehandleSedMottattService behandleSedMottattService;

    private SedMottattJobb sedMottattJobb;

    @BeforeEach
    public void setup() {
        sedMottattJobb = new SedMottattJobb(sedMottattService, behandleSedMottattService);
    }

    @Test
    void sedMottattJobb_4HendelserSkalKjøres_2FeilerOgLagres() {
        SedMottatt feil1 = SedMottatt.av(new SedHendelse());
        feil1.setFeiledeForsok(2);
        SedMottatt feil2 = SedMottatt.av(new SedHendelse());
        feil2.setFeiledeForsok(1);

        SedMottatt sedMottatt1 = SedMottatt.av(new SedHendelse());
        sedMottatt1.setFeiledeForsok(0);
        SedMottatt sedMottatt2 = SedMottatt.av(new SedHendelse());
        sedMottatt2.setFeiledeForsok(1);

        doThrow(new IntegrationException("feil1")).when(behandleSedMottattService).behandleSed(feil1);
        doThrow(new IntegrationException("feil2")).when(behandleSedMottattService).behandleSed(feil2);

        when(sedMottattService.hentAlleUbehandlet()).thenReturn(Arrays.asList(feil1, feil2, sedMottatt1, sedMottatt2));

        sedMottattJobb.sedMottattJobb();

        assertThat(feil1.getFeiledeForsok()).isEqualTo(3);
        assertThat(feil1.isFeilet()).isTrue();
        assertThat(feil2.getFeiledeForsok()).isEqualTo(2);
        assertThat(feil2.isFeilet()).isFalse();
        assertThat(sedMottatt1.getFeiledeForsok()).isZero();
        assertThat(sedMottatt2.getFeiledeForsok()).isEqualTo(1);

        verify(sedMottattService, times(4)).lagre(any());
    }
}
