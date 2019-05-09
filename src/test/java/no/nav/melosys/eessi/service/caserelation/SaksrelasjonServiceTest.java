package no.nav.melosys.eessi.service.caserelation;

import java.util.Optional;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakKobling;
import no.nav.melosys.eessi.models.RinasakKobling;
import no.nav.melosys.eessi.repository.FagsakKoblingRepository;
import no.nav.melosys.eessi.repository.RinasakKoblingRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SaksrelasjonServiceTest {

    @Mock
    private FagsakKoblingRepository fagsakKoblingRepository;
    @Mock
    private RinasakKoblingRepository rinasakKoblingRepository;

    @InjectMocks
    private SaksrelasjonService saksrelasjonService;

    private final Long GSAK_ID = 123L;
    private final String RINA_ID = "321";

    @Before
    public void setUp() throws Exception {
        RinasakKobling rinasakKobling = new RinasakKobling();
        rinasakKobling.setFagsakKobling(new FagsakKobling());
        when(rinasakKoblingRepository.save(any(RinasakKobling.class)))
                .thenReturn(rinasakKobling);
    }

    @Test
    public void lagreKobling_ingenEksisterendeKobling_nyRinaOgFagsakKobling() {

        saksrelasjonService.lagreKobling(GSAK_ID, RINA_ID, BucType.LA_BUC_04);

        verify(fagsakKoblingRepository).save(any(FagsakKobling.class));
        verify(rinasakKoblingRepository).save(any(RinasakKobling.class));
    }

    @Test
    public void lagreKobling_eksisterendeKoblingFinnes_nyRinaKobling() {
        when(fagsakKoblingRepository.findByGsakSaksnummer(eq(GSAK_ID)))
                .thenReturn(Optional.of(new FagsakKobling()));

        saksrelasjonService.lagreKobling(GSAK_ID, RINA_ID, BucType.LA_BUC_04);

        verify(rinasakKoblingRepository).save(any(RinasakKobling.class));
    }

    @Test
    public void finnVedRinaId_verifiserRepositoryKall() {
        saksrelasjonService.finnVedRinaId(RINA_ID);
        verify(rinasakKoblingRepository).findByRinaId(eq(RINA_ID));
    }

    @Test
    public void slettRinaId_verifiserRepositoryKall() {
        saksrelasjonService.slettRinaId(RINA_ID);
        verify(rinasakKoblingRepository).deleteByRinaId(eq(RINA_ID));
    }
}