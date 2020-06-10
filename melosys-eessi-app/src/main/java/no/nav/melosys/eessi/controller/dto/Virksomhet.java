package no.nav.melosys.eessi.controller.dto;

import lombok.Data;
import no.nav.melosys.eessi.models.sed.nav.Arbeidsgiver;
import no.nav.melosys.eessi.models.sed.nav.Identifikator;
import no.nav.melosys.eessi.service.sed.helpers.StreamUtils;

@Data
public class Virksomhet {

    private String navn;
    private Adresse adresse;
    private String orgnr;
    private String type; //Trenger kanskje ikke denne?

    public static Virksomhet av(Arbeidsgiver arbeidsgiver) {
        Virksomhet virksomhet = new Virksomhet();

        virksomhet.navn = arbeidsgiver.getNavn();
        virksomhet.adresse = Adresse.av(arbeidsgiver.getAdresse());
        virksomhet.orgnr = StreamUtils.nullableStream(arbeidsgiver.getIdentifikator())
                .findFirst().map(Identifikator::getId).orElse(null);

        return virksomhet;
    }
}
