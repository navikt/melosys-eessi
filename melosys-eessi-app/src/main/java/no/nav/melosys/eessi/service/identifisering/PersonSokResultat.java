package no.nav.melosys.eessi.service.identifisering;

import javax.validation.constraints.NotNull;
import lombok.Getter;

final class PersonSokResultat {

    @Getter
    private final String ident;
    @Getter
    private final SoekBegrunnelse begrunnelse;

    private PersonSokResultat(String ident, SoekBegrunnelse begrunnelse) {
        this.ident = ident;
        this.begrunnelse = begrunnelse;
    }

    boolean personIdentifisert() {
        return begrunnelse == SoekBegrunnelse.IDENTIFISERT;
    }

    static PersonSokResultat identifisert(@NotNull String ident) {
        return new PersonSokResultat(ident, SoekBegrunnelse.IDENTIFISERT);
    }

    static PersonSokResultat ikkeIdentifisert(SoekBegrunnelse begrunnelse) {
        if (begrunnelse == SoekBegrunnelse.IDENTIFISERT) {
            throw new IllegalArgumentException("Begrunnelse " + begrunnelse + " gjelder ikke når person ikke er identifsert!");
        }
        return new PersonSokResultat(null, begrunnelse);
    }
}
