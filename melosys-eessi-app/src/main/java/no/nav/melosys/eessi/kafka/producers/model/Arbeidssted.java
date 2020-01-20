package no.nav.melosys.eessi.kafka.producers.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
public class Arbeidssted {
    private String navn;
    private Adresse adresse;
    private String hjemmebase;
    private boolean erIkkeFastAdresse;

    public Arbeidssted(no.nav.melosys.eessi.models.sed.nav.Arbeidssted arbeidssted) {
        this(arbeidssted.getNavn(),
            new no.nav.melosys.eessi.kafka.producers.model.Adresse(arbeidssted.getAdresse()),
            arbeidssted.getHjemmebase(),
            StringUtils.equals(arbeidssted.getErikkefastadresse(), "1"));
    }
}
