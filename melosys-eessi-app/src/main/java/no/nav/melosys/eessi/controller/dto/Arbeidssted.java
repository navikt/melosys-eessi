package no.nav.melosys.eessi.controller.dto;

import lombok.Data;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;

@Data
public class Arbeidssted {

    private String navn;
    private Adresse adresse;
    private boolean fysisk;
    private String hjemmebase;

    public static Arbeidssted av(no.nav.melosys.eessi.models.sed.nav.Arbeidssted arbeidsstedFraRina) {
        Arbeidssted arbeidssted = new Arbeidssted();

        arbeidssted.navn = arbeidsstedFraRina.getNavn();
        arbeidssted.adresse = Adresse.av(arbeidsstedFraRina.getAdresse());
        arbeidssted.fysisk = mapFysisk(arbeidsstedFraRina.getErikkefastadresse());
        arbeidssted.hjemmebase = LandkodeMapper.mapTilNavLandkode(arbeidsstedFraRina.getHjemmebase());

        return arbeidssted;
    }

    public static boolean mapFysisk(String erIkkefastadresse) {
        return "nei".equalsIgnoreCase(erIkkefastadresse);
    }
}
