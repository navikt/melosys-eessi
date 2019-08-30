package no.nav.melosys.eessi.service.gsak;

import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.integration.gsak.sak.SakConsumer;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GsakServiceTest {

    @Mock
    private SakConsumer sakConsumer;
    @Mock
    private SaksrelasjonService saksrelasjonService;

    private GsakService gsakService;

    @Before
    public void setup() {
        gsakService = new GsakService(sakConsumer, saksrelasjonService);
    }

    @Test
    public void hentSak_forventSak() throws Exception{
        when(sakConsumer.getSak(anyLong())).thenReturn(new Sak());
        Sak sak = gsakService.hentsak(1L);
        assertThat(sak).isNotNull();
    }
}