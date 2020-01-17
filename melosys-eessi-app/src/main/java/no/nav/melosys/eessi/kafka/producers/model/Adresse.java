package no.nav.melosys.eessi.kafka.producers.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Adresse {
    private String by;
    private String bygning;
    private String gate;
    private String land;
    private String postnummer;
    private String region;
    private String type;

    public Adresse(no.nav.melosys.eessi.models.sed.nav.Adresse adresse) {
        this(adresse.getBy(), adresse.getBygning(), adresse.getGate(), adresse.getLand(),
            adresse.getPostnummer(), adresse.getRegion(), adresse.getType());
    }
}
