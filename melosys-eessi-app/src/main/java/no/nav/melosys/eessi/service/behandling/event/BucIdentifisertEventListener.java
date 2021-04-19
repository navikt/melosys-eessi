package no.nav.melosys.eessi.service.behandling.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class BucIdentifisertEventListener {

    @EventListener
    public void personIdentifisertForBuc(BucIdentifisertEvent bucIdentifisertEvent) {
        log.info("Identifiserer alle SEDer for BUC {}", bucIdentifisertEvent.getBucId());
        // TODO: For alle SEDer til BUC
        //  publiser melding med identifisert ident til kafka
        //  oppdater SedMottattHendelseRepository
    }
}
