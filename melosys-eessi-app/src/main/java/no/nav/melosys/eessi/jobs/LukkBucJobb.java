package no.nav.melosys.eessi.jobs;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import no.nav.melosys.eessi.closebuc.BucLukker;
import no.nav.melosys.eessi.models.BucType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LukkBucJobb {

    private final BucLukker bucLukker;

    public LukkBucJobb(BucLukker bucLukker) {
        this.bucLukker = bucLukker;
    }

    //00:00 hver dag
    @Scheduled(cron = "0 0 0 * * *")
    @SchedulerLock(name = "closeBuc", lockAtLeastFor = "10m", lockAtMostFor = "120m")
    public void closeBuc() {

        Arrays.stream(BucType.values())
                .filter(bucType -> bucType != BucType.LA_BUC_01 && bucType.erLovvalgBuc())
                .forEach(bucLukker::lukkBucerAvType);
    }
}
