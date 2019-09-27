package no.nav.melosys.eessi.jobs;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.melosys.eessi.closebuc.BucCloser;
import no.nav.melosys.eessi.models.BucType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LukkBucJobb {

    private final BucCloser bucCloser;

    public LukkBucJobb(BucCloser bucCloser) {
        this.bucCloser = bucCloser;
    }

    //00:00 hver dag
    @Scheduled(cron = "0 0 0 * * *")
    @SchedulerLock(name = "closeBuc", lockAtLeastForString = "PT10M", lockAtMostForString = "PT120M")
    public void closeBuc() {

        Arrays.stream(BucType.values())
                .filter(bucType -> bucType != BucType.LA_BUC_01)
                .forEach(bucCloser::closeBucsByType);
    }
}
