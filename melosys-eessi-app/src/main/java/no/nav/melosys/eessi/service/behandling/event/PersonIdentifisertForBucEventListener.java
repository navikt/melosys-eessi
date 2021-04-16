package no.nav.melosys.eessi.service.behandling.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@AllArgsConstructor
public class PersonIdentifisertForBucEventListener {

    @TransactionalEventListener
    public void personIdentifisertForBuc(PersonIdentifisertForBucEvent personIdentifisertForBucEvent) {
        log.info("Identifiserer alle SEDer for BUC {}", personIdentifisertForBucEvent.getBucId());
        // TODO: For alle SEDer til BUC
        //  publiser melding med identifisert ident til kafka
        //  oppdater SedMottattHendelseRepository
    }
}
