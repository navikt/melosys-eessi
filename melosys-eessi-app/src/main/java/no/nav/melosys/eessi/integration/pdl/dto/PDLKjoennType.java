package no.nav.melosys.eessi.integration.pdl.dto;

import no.nav.melosys.eessi.models.person.Kjønn;

public enum PDLKjoennType {
    MANN, KVINNE, UKJENT;

    public Kjønn tilDomene() {
        return switch (this) {
            case MANN -> Kjønn.MANN;
            case KVINNE -> Kjønn.KVINNE;
            case UKJENT -> Kjønn.UKJENT;
        };
    }
}
