package no.nav.melosys.eessi.service.tps.personsok;

import lombok.Data;

@Data
public class PersonSoekResponse {

    private String diskresjonskode;
    private Kjoenn kjoenn;
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
    private String sammensattNavn;
    private String ident;

    public enum Kjoenn {
        MANN, KVINNE, UKJENT
    }
}
