package no.nav.melosys.eessi.jobs;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.melosys.eessi.service.eux.EuxService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CloseBucScheduler {

    private final EuxService euxService;

    public CloseBucScheduler(EuxService euxService) {
        this.euxService = euxService;
    }

    @Scheduled(cron = "0 0 0 * * *") // Midnight every day
    @SchedulerLock(name = "closeBuc", lockAtLeastForString = "PT10M", lockAtMostForString = "PT120M")
    public void closeBuc() {
        log.info("SCHEDULER STARTED");
    }
}
