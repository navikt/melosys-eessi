package no.nav.melosys.eessi.service.behandling.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BucIdentifisertEvent extends ApplicationEvent {

    private final String bucId;
    private final String navIdent;

    public BucIdentifisertEvent(String bucId, String navIdent) {
        super(bucId);
        this.bucId = bucId;
        this.navIdent = navIdent;
    }
}
