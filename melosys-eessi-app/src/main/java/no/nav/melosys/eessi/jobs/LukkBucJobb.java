package no.nav.melosys.eessi.jobs;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import no.finn.unleash.Unleash;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.service.buc.LukkBucService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LukkBucJobb {

    private final LukkBucService lukkBucService;
    private final Unleash unleash;

    public LukkBucJobb(LukkBucService lukkBucService, Unleash unleash) {
        this.lukkBucService = lukkBucService;
        this.unleash = unleash;
    }

    //00:00 hver dag
    @Scheduled(cron = "0 0 0 * * *")
    @SchedulerLock(name = "closeBuc", lockAtLeastFor = "10m", lockAtMostFor = "120m")
    public void lukkBuc() {

        Arrays.stream(BucType.values())
                .filter(this::lukkBucPredicate)
                .forEach(lukkBucService::lukkBucerAvType);
    }

    private boolean lukkBucPredicate(BucType bucType) {
        return unleash.isEnabled("melosys.eessi.lukk_la_buc_01_automatisk")
                ? bucType.erLovvalgBuc()
                : bucType.erLovvalgBuc() && bucType != BucType.LA_BUC_01;
    }
}
