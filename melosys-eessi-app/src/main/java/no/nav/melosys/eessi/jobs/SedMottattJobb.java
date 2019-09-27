package no.nav.melosys.eessi.jobs;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.melosys.eessi.models.SedMottatt;
import no.nav.melosys.eessi.service.behandling.BehandleSedMottattService;
import no.nav.melosys.eessi.service.sed.SedMottattService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SedMottattJobb {

    private final SedMottattService sedMottattService;
    private final BehandleSedMottattService behandleSedMottattService;

    public SedMottattJobb(SedMottattService sedMottattService,
            BehandleSedMottattService behandleSedMottattService) {
        this.sedMottattService = sedMottattService;
        this.behandleSedMottattService = behandleSedMottattService;
    }

    @Scheduled(cron = "0 0,30 * * * *")
    @SchedulerLock(name = "behandleSedMottatt", lockAtMostForString = "PT1M", lockAtLeastForString = "PT20S")
    public void sedMottattScheduler() {
        Collection<SedMottatt> mottatteSeder = sedMottattService.hentAlleUbehandlet();
        log.debug("Behandler mottatt {} SED'er", mottatteSeder.size());
        mottatteSeder.forEach(this::behandleSedMottatt);
    }

    @Async
    public void behandleSedMottatt(SedMottatt sedMottatt) {

        try {
            behandleSedMottattService.behandleSed(sedMottatt);
            sedMottatt.setFerdig(true);
            sedMottattService.lagre(sedMottatt);
        } catch (Exception e) {
            log.error("Feil ved behandling av SED {} i rinasak {}", sedMottatt.getSedHendelse().getRinaDokumentId(),
                    sedMottatt.getSedHendelse().getRinaSakId(), e);
            sedMottatt.setFeiledeForsok(sedMottatt.getFeiledeForsok() + 1);
            sedMottattService.lagre(sedMottatt);
        }
    }
}
