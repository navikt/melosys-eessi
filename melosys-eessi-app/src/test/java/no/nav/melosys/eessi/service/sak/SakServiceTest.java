package no.nav.melosys.eessi.service.sak;

import java.util.Optional;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.integration.sak.SakConsumer;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SakServiceTest {

    @Mock
    private SakConsumer sakConsumer;
    @Mock
    private SaksrelasjonService saksrelasjonService;

    private SakService sakService;

    @Before
    public void setup() throws Exception {
        sakService = new SakService(sakConsumer, saksrelasjonService);
        when(sakConsumer.getSak(anyString())).thenReturn(new Sak());
    }

    @Test
    public void hentSak_forventSak() throws Exception{
        Sak sak = sakService.hentsak(1L);
        assertThat(sak).isNotNull();
    }

    @Test
    public void finnSakForRinaSaksnummer_saksrelasjonLagret_forventSak() throws Exception {
        FagsakRinasakKobling fagsakRinasakKobling = new FagsakRinasakKobling();
        fagsakRinasakKobling.setGsakSaksnummer(123L);
        when(saksrelasjonService.søkEtterSaksnummerFraRinaSaksnummer(anyString())).thenReturn(Optional.of(123L));
        assertThat(sakService.finnSakForRinaSaksnummer("123")).isPresent();
    }

    @Test
    public void finnSakForRinaSaksnummer_saksrelasjonLagretHosEux_forventSak() throws Exception {
        when(saksrelasjonService.søkEtterSaksnummerFraRinaSaksnummer(anyString())).thenReturn(Optional.of(123L));
        assertThat(sakService.finnSakForRinaSaksnummer("123321")).isPresent();
    }
}