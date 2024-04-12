package no.nav.melosys.eessi.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class Arbeidsland {

    private String land;
    private List<Arbeidssted> arbeidssted;
    private String harfastarbeidssted;

    public static Arbeidsland av(no.nav.melosys.eessi.models.sed.nav.Arbeidsland arbeidslandFraRina) {
        Arbeidsland arbeidsland = new Arbeidsland();

        arbeidsland.land = arbeidslandFraRina.getLand();
        arbeidsland.arbeidssted = arbeidslandFraRina.getArbeidssted().stream().map(Arbeidssted::av).toList();
        arbeidsland.harfastarbeidssted = arbeidsland.getHarfastarbeidssted();

        return arbeidsland;
    }
}
