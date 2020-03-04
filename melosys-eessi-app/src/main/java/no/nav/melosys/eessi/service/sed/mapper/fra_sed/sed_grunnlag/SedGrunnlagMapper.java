package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.melosys.eessi.controller.dto.*;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Arbeidsgiver;
import no.nav.melosys.eessi.models.sed.nav.Nav;
import no.nav.melosys.eessi.models.sed.nav.Pin;
import no.nav.melosys.eessi.models.sed.nav.Selvstendig;

public interface SedGrunnlagMapper {
     default SedGrunnlagDto map(SED sed) {
        Nav nav = sed.getNav();
        SedGrunnlagDto sedGrunnlagDto = new SedGrunnlagDto();

        sedGrunnlagDto.setBostedsadresse(mapBosted(nav.getBruker().getAdresse()));
        sedGrunnlagDto.setUtenlandskIdent(mapUtenlandskIdent(nav.getBruker().getPerson().getPin()));
        sedGrunnlagDto.setArbeidssteder(mapArbeidssteder(nav.getArbeidssted()));
        sedGrunnlagDto.setArbeidsgivendeVirksomheter(mapVirksomheter(nav.getArbeidsgiver()));
        sedGrunnlagDto.setSelvstendigeVirksomheter(mapSelvstendig(nav.getSelvstendig()));
        sedGrunnlagDto.setYtterligereInformasjon(nav.getYtterligereinformasjon());

        return sedGrunnlagDto;
    }

    default Adresse mapBosted(List<no.nav.melosys.eessi.models.sed.nav.Adresse> adresser) { // todo null-safe
        return adresser.stream()
                .filter(SedGrunnlagMapper::erBostedsadresse).findFirst()
                .map(Adresse::av)
                .orElse(mapAdresse(adresser));
    }

    static boolean erBostedsadresse(no.nav.melosys.eessi.models.sed.nav.Adresse adresse) { // todo null-safe
        return Adressetype.BOSTEDSADRESSE.getAdressetypeRina().equalsIgnoreCase(adresse.getType());
    }

    default Adresse mapAdresse(List<no.nav.melosys.eessi.models.sed.nav.Adresse> adresser) {
        return nullableStream(adresser).findFirst().map(Adresse::av).orElseGet(Adresse::new);
    }

    default List<Ident> mapUtenlandskIdent(List<Pin> pins) {
        return nullableStream(pins).map(Ident::av).filter(Ident::erUtenlandsk).collect(Collectors.toList());
    }

    default List<Arbeidssted> mapArbeidssteder(List<no.nav.melosys.eessi.models.sed.nav.Arbeidssted> arbeidssted) {
        return nullableStream(arbeidssted).map(Arbeidssted::av).collect(Collectors.toList());
    }

    default List<Virksomhet> mapVirksomheter(List<Arbeidsgiver> arbeidsgivere) {
        return nullableStream(arbeidsgivere).map(Virksomhet::av).collect(Collectors.toList());
    }

    default List<Virksomhet> mapSelvstendig(Selvstendig selvstendig) {
        return Optional.ofNullable(selvstendig).stream()
                .map(Selvstendig::getArbeidsgiver)
                .flatMap(Collection::stream)
                .map(Virksomhet::av)
                .collect(Collectors.toList());
    }

    private static <T> Stream<T> nullableStream(Collection<T> collection) {
        return Optional.ofNullable(collection).stream().flatMap(Collection::stream);
    }
}
