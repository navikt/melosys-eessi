package no.nav.melosys.eessi.jobs;

import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.service.buc.BucLukker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LukkBucJobbTest {

    @Mock
    private BucLukker bucLukker;
    @InjectMocks
    private LukkBucJobb lukkBucJobb;

    @Test
    public void closeBuc_verifiserKorrekteBucTyperSkalLukkes() {
        lukkBucJobb.closeBuc();

        verify(bucLukker).lukkBucerAvType(BucType.LA_BUC_02);
        verify(bucLukker).lukkBucerAvType(BucType.LA_BUC_03);
        verify(bucLukker).lukkBucerAvType(BucType.LA_BUC_04);
        verify(bucLukker).lukkBucerAvType(BucType.LA_BUC_05);
        verify(bucLukker).lukkBucerAvType(BucType.LA_BUC_06);
    }
}
