package no.nav.melosys.eessi.controller.dto;

import lombok.Data;
import no.nav.melosys.eessi.models.sed.nav.Pin;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;

@Data
public class Ident {

    private String ident;
    private String landkode;

    public static Ident av(Pin pin) {
        Ident ident = new Ident();
        ident.ident = pin.getIdentifikator();
        ident.landkode = LandkodeMapper.mapTilNavLandkode(pin.getLand());
        return ident;
    }

    public boolean erNorsk() {
        return "NO".equalsIgnoreCase(landkode);
    }

    public boolean erUtenlandsk() {
        return !erNorsk();
    }
}
