package no.nav.melosys.eessi.service.mottak;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.BucIdentifiseringService;
import no.nav.melosys.eessi.identifisering.event.BucIdentifisertEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class BucIdentifisertEventListener {

    private final BucIdentifiseringService bucIdentifiseringService;

    @EventListener
    public void personIdentifisertForBuc(BucIdentifisertEvent bucIdentifisertEvent) {
        log.info("Identifiserer alle SEDer for BUC {}", bucIdentifisertEvent.getBucId());
        bucIdentifiseringService.bucIdentifisert(bucIdentifisertEvent.getBucId(), bucIdentifisertEvent.getAktørId());
    }
}
