package no.nav.melosys.eessi.service.behandling.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import no.nav.melosys.eessi.service.joark.event.OpprettJournalpostEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@AllArgsConstructor
public class PersonIdentifisertForBucEventListener {

    @TransactionalEventListener
    public void personIdentifisertForBuc(PersonIdentifisertForBucEvent personIdentifisertForBucEvent) {
        log.info("Identifiserer alle SEDer for BUC {}", personIdentifisertForBucEvent.getBucId());



    }
}
