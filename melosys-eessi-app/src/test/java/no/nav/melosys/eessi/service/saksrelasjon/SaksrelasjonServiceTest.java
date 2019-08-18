package no.nav.melosys.eessi.service.saksrelasjon;

import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.repository.FagsakRinasakKoblingRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SaksrelasjonServiceTest {

    @Mock
    private FagsakRinasakKoblingRepository fagsakRinasakKoblingRepository;

    @InjectMocks
    private SaksrelasjonService saksrelasjonService;

    private final String RINA_ID = "321";

    @Test
    public void lagreKobling_verifiserRepositoryKall() {
        saksrelasjonService.lagreKobling(123L, RINA_ID, BucType.LA_BUC_04);
        verify(fagsakRinasakKoblingRepository).save(any(FagsakRinasakKobling.class));
    }

    @Test
    public void finnVedRinaId_verifiserRepositoryKall() {
        saksrelasjonService.finnVedRinaId(RINA_ID);
        verify(fagsakRinasakKoblingRepository).findByRinaSaksnummer(eq(RINA_ID));
    }

    @Test
    public void slettRinaId_verifiserRepositoryKall() {
        saksrelasjonService.slettVedRinaId(RINA_ID);
        verify(fagsakRinasakKoblingRepository).deleteByRinaSaksnummer(eq(RINA_ID));
    }

    @Test
    public void finnVedGsakSaksnummer_verifiserRepositoryKall() {
        saksrelasjonService.finnVedGsakSaksnummer(123L);
        verify(fagsakRinasakKoblingRepository).findAllByGsakSaksnummer(eq(123L));
    }
}