package no.nav.melosys.eessi.models.sed.nav;

import lombok.Data;

@Data
public abstract class Vedtak {
    private String datoforrigevedtak;
    private String eropprinneligvedtak; // Pga. RINA regler: Kan bare sette "ja" eller null (default: nei)
    private String erendringsvedtak; // Pga. RINA regler: Kan bare sette "nei" eller null (default: ja)
}
