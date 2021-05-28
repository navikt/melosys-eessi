package no.nav.melosys.eessi.jobs;

import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.service.buc.LukkBucService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LukkBucJobbTest {

    @Mock
    private LukkBucService lukkBucService;

    private LukkBucJobb lukkBucJobb;

    @BeforeEach
    void setup() {
        lukkBucJobb = new LukkBucJobb(lukkBucService);
    }

    @Test
    void closeBuc_verifiserKorrekteBucTyperSkalLukkes() {
        lukkBucJobb.closeBuc();

        verify(lukkBucService).lukkBucerAvType(BucType.LA_BUC_01);
        verify(lukkBucService).lukkBucerAvType(BucType.LA_BUC_02);
        verify(lukkBucService).lukkBucerAvType(BucType.LA_BUC_03);
        verify(lukkBucService).lukkBucerAvType(BucType.LA_BUC_04);
        verify(lukkBucService).lukkBucerAvType(BucType.LA_BUC_05);
        verify(lukkBucService).lukkBucerAvType(BucType.LA_BUC_06);
    }
}
