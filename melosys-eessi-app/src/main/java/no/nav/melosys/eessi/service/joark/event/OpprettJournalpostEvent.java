package no.nav.melosys.eessi.service.joark.event;

import lombok.Getter;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.SedKontekst;
import org.springframework.context.ApplicationEvent;

@Getter
public class OpprettJournalpostEvent extends ApplicationEvent {

    private final SedHendelse sedHendelse;
    private final SedKontekst sedKontekst;

    public OpprettJournalpostEvent(SedHendelse sedHendelse, SedKontekst sedKontekst) {
        super(sedHendelse);
        this.sedHendelse = sedHendelse;
        this.sedKontekst = sedKontekst;
    }
}
