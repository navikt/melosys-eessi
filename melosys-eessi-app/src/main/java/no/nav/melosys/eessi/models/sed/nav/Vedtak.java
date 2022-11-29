package no.nav.melosys.eessi.models.sed.nav;

import lombok.Data;

@Data
public abstract class Vedtak {
    private String datoforrigevedtak;
    private String eropprinneligvedtak; // Kan kun settes til "ja", null er nei
    private String erendringsvedtak; // Kan kun settes til "nei", null er ja
}
