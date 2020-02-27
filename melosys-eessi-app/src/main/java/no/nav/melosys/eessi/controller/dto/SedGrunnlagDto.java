package no.nav.melosys.eessi.controller.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import no.nav.melosys.eessi.models.sed.nav.Arbeidsgiver;
import no.nav.melosys.eessi.models.sed.nav.Nav;
import no.nav.melosys.eessi.models.sed.nav.Pin;

@Data
public class SedGrunnlagDto {
    private List<Ident> utenlandskIdent;
    private Adresse bostedsadresse;
    private List<Virksomhet> arbeidsgivendeVirksomheter;
    private List<Virksomhet> selvstendigeVirksomheter;
    private List<Arbeidssted> arbeidssteder;

    public static SedGrunnlagDto av(Nav nav) {
        SedGrunnlagDto sedGrunnlagDto = new SedGrunnlagDto();

        sedGrunnlagDto.setBostedsadresse(mapBosted(nav.getBruker().getAdresse()));
        sedGrunnlagDto.setUtenlandskIdent(mapUtenlandskIdent(nav.getBruker().getPerson().getPin()));
        sedGrunnlagDto.setArbeidssteder(mapArbeidssteder(nav.getArbeidssted()));
        sedGrunnlagDto.setArbeidsgivendeVirksomheter(mapVirksomheter(nav.getArbeidsgiver()));
        sedGrunnlagDto.setSelvstendigeVirksomheter(mapVirksomheter(nav.getSelvstendig().getArbeidsgiver()));

        return sedGrunnlagDto;
    }

    private static Adresse mapBosted(List<no.nav.melosys.eessi.models.sed.nav.Adresse> adresser) {
        return adresser.stream()
                .filter(SedGrunnlagDto::erBostedsadresse).findFirst()
                .map(Adresse::av)
                .orElse(mapAdresse(adresser));
    }

    private static boolean erBostedsadresse(no.nav.melosys.eessi.models.sed.nav.Adresse adresse) {
        return Adressetype.BOSTEDSADRESSE.getAdressetypeRina().equalsIgnoreCase(adresse.getType());
    }

    private static Adresse mapAdresse(List<no.nav.melosys.eessi.models.sed.nav.Adresse> adresser) {
        return adresser.stream().findFirst().map(Adresse::av).orElseGet(Adresse::new);
    }

    private static List<Ident> mapUtenlandskIdent(List<Pin> pins) {
        return pins.stream().map(Ident::av).filter(Ident::erUtenlandsk).collect(Collectors.toList());
    }

    private static List<Arbeidssted> mapArbeidssteder(List<no.nav.melosys.eessi.models.sed.nav.Arbeidssted> arbeidssted) {
        return arbeidssted.stream().map(Arbeidssted::av).collect(Collectors.toList());
    }

    private static List<Virksomhet> mapVirksomheter(List<Arbeidsgiver> arbeidsgivere) {
        return arbeidsgivere.stream().map(Virksomhet::av).collect(Collectors.toList());
    }
}
