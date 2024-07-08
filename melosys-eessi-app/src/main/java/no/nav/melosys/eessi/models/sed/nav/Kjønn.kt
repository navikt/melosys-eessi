package no.nav.melosys.eessi.models.sed.nav;

public enum Kjønn {
    M, K, U;

    public no.nav.melosys.eessi.models.person.Kjønn tilDomene() {
        return switch (this) {
            case K -> no.nav.melosys.eessi.models.person.Kjønn.KVINNE;
            case M -> no.nav.melosys.eessi.models.person.Kjønn.MANN;
            case U -> no.nav.melosys.eessi.models.person.Kjønn.UKJENT;
        };
    }
}
