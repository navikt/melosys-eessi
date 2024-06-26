package no.nav.melosys.eessi.controller.dto;

import lombok.Data;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import org.apache.commons.lang3.StringUtils;

@Data
public class Adresse {

    private String poststed;
    private String postnr;
    private String land;
    private String gateadresse;
    private String tilleggsnavn;
    private String region;
    private Adressetype adressetype;

    public static Adresse av(no.nav.melosys.eessi.models.sed.nav.Adresse adresseFraRina) {
        if (adresseFraRina == null) {
            return new Adresse();
        }

        Adresse adresse = new Adresse();

        adresse.poststed = adresseFraRina.getBy();
        adresse.postnr = adresseFraRina.getPostnummer();
        adresse.land = LandkodeMapper.mapTilNavLandkode(adresseFraRina.getLand());
        adresse.gateadresse = String.format("%s %s",
            StringUtils.defaultIfEmpty(adresseFraRina.getGate(), ""),
            StringUtils.defaultIfEmpty(adresseFraRina.getBygning(), "")
        ).trim();
        adresse.tilleggsnavn = StringUtils.defaultIfEmpty(adresseFraRina.getBygning(), "");
        adresse.region = adresseFraRina.getRegion();
        adresse.adressetype = Adressetype.fraAdressetypeRina(adresseFraRina.getType());

        return adresse;
    }
}
