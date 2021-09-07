package no.nav.melosys.eessi.identifisering.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BucIdentifisertEvent extends ApplicationEvent {

    private final String bucId;
    private final String ident;

    public BucIdentifisertEvent(String bucId, String ident) {
        super(bucId);
        this.bucId = bucId;
        this.ident = ident;
    }
}
