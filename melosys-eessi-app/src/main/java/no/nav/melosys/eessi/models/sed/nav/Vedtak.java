package no.nav.melosys.eessi.models.sed.nav;

import lombok.Data;

@Data
public abstract class Vedtak {
    private String datoforrigevedtak;
    private String eropprinneligvedtak; // RINA regler: Kan bare sette "ja" eller null (default: null, som betyr nei)
    private String erendringsvedtak; // RINA regler: Kan bare sette "nei" eller null (default: null, som betyr ja)
}
