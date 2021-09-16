package no.nav.melosys.eessi.service.saksrelasjon;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.BucIdentifisertService;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.service.sak.ArkivsakService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class SaksrelasjonOpprettetEventListener {

    private final ArkivsakService arkivsakService;
    private final PersonFasade personFasade;
    private final BucIdentifisertService bucIdentifisertService;

    public SaksrelasjonOpprettetEventListener(ArkivsakService arkivsakService,
                                              PersonFasade personFasade,
                                              BucIdentifisertService bucIdentifisertService) {
        this.arkivsakService = arkivsakService;
        this.personFasade = personFasade;
        this.bucIdentifisertService = bucIdentifisertService;
    }

    @Async
    @TransactionalEventListener
    public void saksrelasjonOpprettet(SaksrelasjonOpprettetEvent event) {
        final var norskIdent = personFasade.hentNorskIdent(
            arkivsakService.hentsak(event.getArkivsakID()).getAktoerId()
        );

        bucIdentifisertService.lagreIdentifisertPerson(
            event.getRinaSaksnummer(),
            norskIdent
        );
    }
}
