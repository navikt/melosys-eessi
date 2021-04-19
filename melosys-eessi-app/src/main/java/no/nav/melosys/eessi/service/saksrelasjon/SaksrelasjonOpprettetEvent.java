package no.nav.melosys.eessi.service.saksrelasjon;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SaksrelasjonOpprettetEvent extends ApplicationEvent {

    private final String rinaSaksnummer;
    private final Long arkivsakID;

    public SaksrelasjonOpprettetEvent(String rinaSaksnummer, Long arkivsakID) {
        super(rinaSaksnummer);
        this.rinaSaksnummer = rinaSaksnummer;
        this.arkivsakID = arkivsakID;
    }
}
