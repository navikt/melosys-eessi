package no.nav.melosys.eessi.kafka.producers.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
@AllArgsConstructor
public class Arbeidsland {
    private String land;
    private List<Arbeidssted> arbeidssted;
    private boolean harfastarbeidssted;

    public Arbeidsland(no.nav.melosys.eessi.models.sed.nav.Arbeidsland arbeidsland) {
        this(arbeidsland.getLand(),
            arbeidsland.getArbeidssted().stream().map(Arbeidssted::new).toList(),
            !StringUtils.equals(arbeidsland.getHarfastarbeidssted(), "1"));
    }
}
