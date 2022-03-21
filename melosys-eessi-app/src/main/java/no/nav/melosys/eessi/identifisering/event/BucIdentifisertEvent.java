package no.nav.melosys.eessi.identifisering.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BucIdentifisertEvent extends ApplicationEvent {

    private final String bucId;
    private final String folkeregisterident;

    public BucIdentifisertEvent(String bucId, String folkeregisterident) {
        super(bucId);
        this.bucId = bucId;
        this.folkeregisterident = folkeregisterident;
    }
}
