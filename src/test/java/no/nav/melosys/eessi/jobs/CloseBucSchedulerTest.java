package no.nav.melosys.eessi.jobs;

import no.nav.melosys.eessi.closebuc.BucCloser;
import no.nav.melosys.eessi.models.BucType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CloseBucSchedulerTest {

    @Mock
    private BucCloser bucCloser;
    @InjectMocks
    private CloseBucScheduler closeBucScheduler;

    @Test
    public void closeBuc_verifiserKorrekteBucTyperSkalLukkes() {
        closeBucScheduler.closeBuc();

        verify(bucCloser).closeBucsByType(BucType.LA_BUC_02);
        verify(bucCloser).closeBucsByType(BucType.LA_BUC_03);
        verify(bucCloser).closeBucsByType(BucType.LA_BUC_04);
        verify(bucCloser).closeBucsByType(BucType.LA_BUC_05);
        verify(bucCloser).closeBucsByType(BucType.LA_BUC_06);
    }
}