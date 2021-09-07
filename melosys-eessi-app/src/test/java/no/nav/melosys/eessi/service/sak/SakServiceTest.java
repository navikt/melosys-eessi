package no.nav.melosys.eessi.service.sak;

import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.integration.sak.SakConsumer;
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

    private SakService sakService;

    @BeforeEach
    public void setup() throws Exception {
        sakService = new SakService(sakConsumer);
        when(sakConsumer.getSak(anyString())).thenReturn(new Sak());
    }

    @Test
    void hentSak_forventSak() {
        Sak sak = sakService.hentsak(1L);
        assertThat(sak).isNotNull();
    }
}
