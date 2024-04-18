package no.nav.melosys.eessi.service.mottak;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.BehandleBucIdentifisertService;
import no.nav.melosys.eessi.identifisering.event.BucIdentifisertEvent;
import no.nav.melosys.eessi.integration.PersonFasade;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
@Profile("!local-q2")
public class BucIdentifisertEventListener {

    private final BehandleBucIdentifisertService behandleBucIdentifisertService;

    @EventListener
    public void personIdentifisertForBuc(BucIdentifisertEvent bucIdentifisertEvent) {
        log.info("Identifiserer alle SEDer for BUC {}", bucIdentifisertEvent.getBucId());
        behandleBucIdentifisertService.bucIdentifisert(
            bucIdentifisertEvent.getBucId(),
            bucIdentifisertEvent.getFolkeregisterident()
        );
    }
}
