package no.nav.melosys.eessi.service.gsak;

import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.integration.gsak.sak.SakConsumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GsakServiceTest {

    @Mock
    private SakConsumer sakConsumer;
    @InjectMocks
    private GsakService gsakService;

    @Test
    public void getSak_expectSak() throws Exception{
        when(sakConsumer.getSak(anyLong())).thenReturn(new Sak());
        Sak sak = gsakService.hentsak(1L);
        assertThat(sak).isNotNull();
    }
}