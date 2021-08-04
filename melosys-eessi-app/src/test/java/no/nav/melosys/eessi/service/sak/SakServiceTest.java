package no.nav.melosys.eessi.service.sak;

import java.util.Optional;

import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.integration.sak.SakConsumer;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SakServiceTest {

    @Mock
    private SakConsumer sakConsumer;
    @Mock
    private SaksrelasjonService saksrelasjonService;

    private SakService sakService;

    @BeforeEach
    public void setup() throws Exception {
        sakService = new SakService(sakConsumer, saksrelasjonService);
        when(sakConsumer.getSak(anyString())).thenReturn(new Sak());
    }

    @Test
    void hentSak_forventSak() {
        Sak sak = sakService.hentsak(1L);
        assertThat(sak).isNotNull();
    }

    @Test
    void finnSakForRinaSaksnummer_saksrelasjonLagret_forventSak() {
        FagsakRinasakKobling fagsakRinasakKobling = new FagsakRinasakKobling();
        fagsakRinasakKobling.setGsakSaksnummer(123L);
        when(saksrelasjonService.søkEtterSaksnummerFraRinaSaksnummer(anyString())).thenReturn(Optional.of(123L));
        assertThat(sakService.finnSakForRinaSaksnummer("123")).isPresent();
    }

    @Test
    void finnSakForRinaSaksnummer_saksrelasjonLagretHosEux_forventSak() {
        when(saksrelasjonService.søkEtterSaksnummerFraRinaSaksnummer(anyString())).thenReturn(Optional.of(123L));
        assertThat(sakService.finnSakForRinaSaksnummer("123321")).isPresent();
    }
}
