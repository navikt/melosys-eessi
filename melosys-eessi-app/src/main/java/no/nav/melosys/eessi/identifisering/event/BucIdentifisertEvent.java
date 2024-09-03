// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.identifisering.event;

import org.springframework.context.ApplicationEvent;

public class BucIdentifisertEvent extends ApplicationEvent {
    private final String bucId;
    private final String folkeregisterident;

    public BucIdentifisertEvent(String bucId, String folkeregisterident) {
        super(bucId);
        this.bucId = bucId;
        this.folkeregisterident = folkeregisterident;
    }

    @java.lang.SuppressWarnings("all")
    public String getBucId() {
        return this.bucId;
    }

    @java.lang.SuppressWarnings("all")
    public String getFolkeregisterident() {
        return this.folkeregisterident;
    }
}
