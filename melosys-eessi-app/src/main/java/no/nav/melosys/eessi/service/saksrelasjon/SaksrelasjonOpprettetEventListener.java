package no.nav.melosys.eessi.service.saksrelasjon;

import lombok.AllArgsConstructor;
import no.nav.melosys.eessi.identifisering.event.BucIdentifisertEvent;
import no.nav.melosys.eessi.integration.sak.SakConsumer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class SaksrelasjonOpprettetEventListener {

    private final SakConsumer sakConsumer;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Async
    @TransactionalEventListener
    public void saksrelasjonOpprettet(SaksrelasjonOpprettetEvent event) {
        var sak = sakConsumer.getSak(event.getArkivsakID().toString());
        applicationEventPublisher.publishEvent(new BucIdentifisertEvent(event.getRinaSaksnummer(), sak.getAktoerId()));
    }
}
