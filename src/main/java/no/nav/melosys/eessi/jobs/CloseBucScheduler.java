package no.nav.melosys.eessi.jobs;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.melosys.eessi.closebuc.BucCloser;
import no.nav.melosys.eessi.models.sed.BucType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CloseBucScheduler {

    private final BucCloser bucCloser;

    public CloseBucScheduler(BucCloser bucCloser) {
        this.bucCloser = bucCloser;
    }

    @Scheduled(cron = "0 0 0 * * *") // Midnight every day
    @SchedulerLock(name = "closeBuc", lockAtLeastForString = "PT10M", lockAtMostForString = "PT120M")
    public void closeBuc() {
        List<BucType> bucTyper = Arrays.asList(BucType.values());
        bucTyper.remove(BucType.LA_BUC_01);

        for (BucType buc : bucTyper) {
            bucCloser.closeBucsByType(buc);
        }
    }
}
