// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.service.mottak;

import no.nav.melosys.eessi.identifisering.BehandleBucIdentifisertService;
import no.nav.melosys.eessi.identifisering.event.BucIdentifisertEvent;
import no.nav.melosys.eessi.integration.PersonFasade;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("!local-q2")
public class BucIdentifisertEventListener {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BucIdentifisertEventListener.class);
    private final BehandleBucIdentifisertService behandleBucIdentifisertService;

    @EventListener
    public void personIdentifisertForBuc(BucIdentifisertEvent bucIdentifisertEvent) {
        log.info("Identifiserer alle SEDer for BUC {}", bucIdentifisertEvent.getBucId());
        behandleBucIdentifisertService.bucIdentifisert(bucIdentifisertEvent.getBucId(), bucIdentifisertEvent.getFolkeregisterident());
    }

    @java.lang.SuppressWarnings("all")
    public BucIdentifisertEventListener(final BehandleBucIdentifisertService behandleBucIdentifisertService) {
        this.behandleBucIdentifisertService = behandleBucIdentifisertService;
    }
}
