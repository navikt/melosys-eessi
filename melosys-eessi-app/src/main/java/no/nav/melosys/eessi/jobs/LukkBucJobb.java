package no.nav.melosys.eessi.jobs;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.service.buc.LukkBucService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LukkBucJobb {

    private final LukkBucService lukkBucService;

    public LukkBucJobb(LukkBucService lukkBucService) {
        this.lukkBucService = lukkBucService;
    }

    //00:00 hver dag
    @Scheduled(cron = "0 0 0 * * *")
    @SchedulerLock(name = "closeBuc", lockAtLeastFor = "10m", lockAtMostFor = "120m")
    public void lukkBuc() {

        Arrays.stream(BucType.values())
                .filter(this::bucKanLukkes)
                .forEach(lukkBucService::lukkBucerAvType);
    }

    private boolean bucKanLukkes(BucType bucType) {
        return bucType.erLovvalgBuc();
    }
}
