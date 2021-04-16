package no.nav.melosys.eessi.jobs;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import no.nav.melosys.eessi.models.SedMottatt;
import no.nav.melosys.eessi.service.behandling.BehandleSedMottattService;
import no.nav.melosys.eessi.service.sed.SedMottattService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.config.MDCLogging.loggSedID;
import static no.nav.melosys.eessi.config.MDCLogging.slettSedIDLogging;

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

    @Scheduled(cron = "0 0 * * * *")
    @SchedulerLock(name = "behandleSedMottatt", lockAtMostFor = "1m", lockAtLeastFor = "20s")
    public void sedMottattJobb() {
        Collection<SedMottatt> mottatteSeder = sedMottattService.hentAlleUbehandlet();
        log.debug("Behandler mottatt {} SED'er", mottatteSeder.size());
        mottatteSeder.forEach(this::behandleSedMottatt);
    }

    private void behandleSedMottatt(SedMottatt sedMottatt) {
        try {
            loggSedID(sedMottatt.getSedHendelse().getSedId());
            behandleSedMottattService.behandleSed(sedMottatt);
            sedMottatt.setFerdig(true);
            sedMottattService.lagre(sedMottatt);
        } catch (Exception e) {
            log.error("Feil ved behandling av SED {} i rinasak {}", sedMottatt.getSedHendelse().getRinaDokumentId(),
                    sedMottatt.getSedHendelse().getRinaSakId(), e);
            sedMottatt.setFeiledeForsok(sedMottatt.getFeiledeForsok() + 1);

            if (sedMottatt.getFeiledeForsok() > 2) {
                sedMottatt.setFeilet(true);
            }

            sedMottattService.lagre(sedMottatt);
        } finally {
            slettSedIDLogging();
        }
    }
}
