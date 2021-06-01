package no.nav.melosys.eessi.jobs;

import no.finn.unleash.FakeUnleash;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.service.buc.LukkBucService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LukkBucJobbTest {

    @Mock
    private LukkBucService lukkBucService;
    private FakeUnleash fakeUnleash = new FakeUnleash();

    private LukkBucJobb lukkBucJobb;

    @BeforeEach
    void setup() {
        lukkBucJobb = new LukkBucJobb(lukkBucService, fakeUnleash);
    }

    @Test
    void lukkBuc_featureTogglePå_alleLovvalgBucLukkes() {
        fakeUnleash.enableAll();
        lukkBucJobb.lukkBuc();

        verify(lukkBucService).lukkBucerAvType(BucType.LA_BUC_01);
        verify(lukkBucService).lukkBucerAvType(BucType.LA_BUC_02);
        verify(lukkBucService).lukkBucerAvType(BucType.LA_BUC_03);
        verify(lukkBucService).lukkBucerAvType(BucType.LA_BUC_04);
        verify(lukkBucService).lukkBucerAvType(BucType.LA_BUC_05);
        verify(lukkBucService).lukkBucerAvType(BucType.LA_BUC_06);
    }

    @Test
    void lukkBuc_featureToggleAv_LaBuc01LukkesIkke() {
        fakeUnleash.disableAll();
        lukkBucJobb.lukkBuc();
        verify(lukkBucService, never()).lukkBucerAvType(BucType.LA_BUC_01);
    }

}
