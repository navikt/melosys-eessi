package no.nav.melosys.eessi.service.sed.mapper.til_sed;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import no.nav.melosys.eessi.controller.dto.*;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.Konstanter;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Adresse;
import no.nav.melosys.eessi.models.sed.nav.Arbeidssted;
import no.nav.melosys.eessi.models.sed.nav.Bruker;
import no.nav.melosys.eessi.models.sed.nav.Periode;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import org.springframework.util.StringUtils;

import static no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_G_VER;
import static no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_VER;

/**
 * Felles mapper-interface for alle typer SED. Mapper NAV-objektet i NAV-SED, som brukes av eux for
 * å plukke ut nødvendig informasjon for en angitt SED.
 */
public interface SedMapper {
    default SED mapTilSed(SedDataDto sedData) {
        var sed = new SED();

        sed.setNav(prefillNav(sedData));
        sed.setSedType(getSedType().name());
        sed.setSedGVer(DEFAULT_SED_G_VER);
        sed.setSedVer(DEFAULT_SED_VER);

        return sed;
    }

    SedType getSedType();

    default Nav prefillNav(SedDataDto sedData) {
        var nav = new Nav();

        nav.setBruker(hentBruker(sedData));
        nav.setArbeidssted(hentArbeidssted(sedData));
        nav.setArbeidsgiver(hentArbeidsgivereILand(sedData.getArbeidsgivendeVirksomheter(), sedData.finnLovvalgslandDefaultNO()));
        nav.setYtterligereinformasjon(sedData.getYtterligereInformasjon());

        if (!sedData.getSelvstendigeVirksomheter().isEmpty()) {
            nav.setSelvstendig(hentSelvstendig(sedData));
        }

        return nav;
    }

    default Bruker hentBruker(SedDataDto sedDataDto) {
        var bruker = new Bruker();
        bruker.setPerson(hentPerson(sedDataDto));
        bruker.setAdresse(hentAdresser(sedDataDto));
        setFamiliemedlemmer(sedDataDto, bruker);
        return bruker;
    }

    default Person hentPerson(SedDataDto sedData) {
        var person = new Person();

        person.setFornavn(sedData.getBruker().getFornavn());
        person.setEtternavn(sedData.getBruker().getEtternavn());
        person.setFoedselsdato(formaterDato(sedData.getBruker().getFoedseldato()));
        person.setFoedested(null); //det antas at ikke trengs når NAV fyller ut.
        person.setKjoenn(Kjønn.valueOf(sedData.getBruker().getKjoenn()));

        var statsborgerskap = new Statsborgerskap();
        statsborgerskap.setLand(LandkodeMapper.getLandkodeIso2(sedData.getBruker().getStatsborgerskap()));

        person.setStatsborgerskap(Collections.singletonList(statsborgerskap));

        person.setPin(hentPin(sedData));

        return person;
    }

    default List<Pin> hentPin(SedDataDto sedData) {
        List<Pin> pins = Lists.newArrayList();

        pins.add(new Pin(
                sedData.getBruker().getFnr(), "NO",
                null)); //null settes for sektor per nå. Ikke påkrevd. Evt hardkode 'alle'

        for (Ident utenlandskIdent : sedData.getUtenlandskIdent()) {
            pins.add(
                    new Pin(utenlandskIdent.getIdent(),
                            LandkodeMapper.getLandkodeIso2(utenlandskIdent.getLandkode()), null)
            );
        }

        return pins;
    }

    default List<Adresse> hentAdresser(SedDataDto sedDataDto) {
        List<Adresse> adresser = new ArrayList<>();
        if (sedDataDto.getBostedsadresse() != null) {
            adresser.add(mapBostedsadresse(sedDataDto.getBostedsadresse()));
        }
        if (sedDataDto.getKontaktadresse() != null) {
            adresser.add(mapAdresse(sedDataDto.getKontaktadresse()));
        }
        if (sedDataDto.getOppholdsadresse() != null) {
            adresser.add(mapAdresse(sedDataDto.getOppholdsadresse()));
        }
        return adresser;
    }

    private Adresse mapBostedsadresse(no.nav.melosys.eessi.controller.dto.Adresse adresse) {
        var bostedsadresse = mapAdresse(adresse);
        // ref: punkt 2.1.1 (A001) https://confluence.adeo.no/display/TEESSI/Mapping+av+lovvalgs+SED+til+Melosys+domenemodell
        if (adresse.getAdressetype() == Adressetype.BOSTEDSADRESSE) {
            if ("NO".equalsIgnoreCase(bostedsadresse.getLand())) {
                bostedsadresse.setType(Adressetype.BOSTEDSADRESSE.getAdressetypeRina());
            } else {
                bostedsadresse.setType(Adressetype.POSTADRESSE.getAdressetypeRina());
            }
        }
        return bostedsadresse;
    }

    private Adresse mapAdresse(no.nav.melosys.eessi.controller.dto.Adresse adresse) {
        var bostedsadresse = new Adresse();
        bostedsadresse.setType(adresse.getAdressetype().getAdressetypeRina());
        bostedsadresse.setGate(adresse.getGateadresse());
        bostedsadresse.setBy(adresse.getPoststed());
        bostedsadresse.setPostnummer(adresse.getPostnr());
        bostedsadresse.setRegion(adresse.getRegion());
        bostedsadresse.setLand(LandkodeMapper.getLandkodeIso2(adresse.getLand()));
        return bostedsadresse;
    }

    default void setFamiliemedlemmer(SedDataDto sedData, Bruker bruker) {

        //Splitter per nå navnet etter første mellomrom
        Optional<FamilieMedlem> optionalFar = sedData.getFamilieMedlem().stream()
                .filter(f -> f.getRelasjon().equalsIgnoreCase("FAR")).findFirst();

        if (optionalFar.isPresent()) {
            var far = new Far();
            var person = new Person();
            person.setEtternavnvedfoedsel(optionalFar.get().getEtternavn());
            person.setFornavn(optionalFar.get().getFornavn());

            far.setPerson(person);
            bruker.setFar(far);
        }

        Optional<FamilieMedlem> optionalMor = sedData.getFamilieMedlem().stream()
                .filter(f -> f.getRelasjon().equalsIgnoreCase("MOR")).findFirst();

        if (optionalMor.isPresent()) {
            var mor = new Mor();
            var person = new Person();
            person.setEtternavnvedfoedsel(optionalMor.get().getEtternavn());
            person.setFornavn(optionalMor.get().getFornavn());

            mor.setPerson(person);
            bruker.setMor(mor);
        }
    }

    default List<Arbeidssted> hentArbeidssted(SedDataDto sedData) {

        List<Arbeidssted> arbeidsstedList = Lists.newArrayList();

        for (no.nav.melosys.eessi.controller.dto.Arbeidssted arbStd : sedData.getArbeidssteder()) {
            var arbeidssted = new Arbeidssted();
            arbeidssted.setNavn(arbStd.getNavn());
            arbeidssted.setAdresse(hentAdresseFraDtoAdresse(arbStd.getAdresse()));
            arbeidssted.setHjemmebase(landkodeIso2EllerNull(arbStd.getHjemmebase()));

            if (arbStd.isFysisk()) {
                arbeidssted.setErikkefastadresse("nei");
            } else if (StringUtils.hasText(arbeidssted.getHjemmebase()) || !arbStd.isFysisk()) {
                arbeidssted.setErikkefastadresse("ja");
            }

            arbeidsstedList.add(arbeidssted);
        }

        return arbeidsstedList;
    }

    default List<Arbeidsgiver> hentArbeidsgivereILand(List<Virksomhet> virksomheter, String landkode) {
        return hentArbeidsgiver(virksomheter, v -> Objects.equal(v.getAdresse().getLand(), landkode));
    }

    default List<Arbeidsgiver> hentArbeidsgivereIkkeILand(List<Virksomhet> virksomheter, String landkode) {
        return hentArbeidsgiver(virksomheter, v -> !Objects.equal(v.getAdresse().getLand(), landkode));
    }

    default List<Arbeidsgiver> hentArbeidsgiver(List<Virksomhet> virksomheter, Predicate<Virksomhet> virksomhetPredicate) {
        return virksomheter.stream()
                .filter(virksomhetPredicate)
                .map(this::hentArbeidsgiver)
                .collect(Collectors.toList());
    }

    default Arbeidsgiver hentArbeidsgiver(Virksomhet virksomhet) {
        var arbeidsgiver = new Arbeidsgiver();
        arbeidsgiver.setNavn(virksomhet.getNavn());
        arbeidsgiver.setAdresse(hentAdresseFraDtoAdresse(virksomhet.getAdresse()));
        arbeidsgiver.setIdentifikator(lagIdentifikator(virksomhet.getOrgnr()));
        return arbeidsgiver;
    }

    default Selvstendig hentSelvstendig(SedDataDto sedData) {

        var selvstendig = new Selvstendig();
        List<Arbeidsgiver> arbeidsgiverList = Lists.newArrayList();

        for (Virksomhet v : sedData.getSelvstendigeVirksomheter()) {
            var arbeidsgiver = new Arbeidsgiver();

            arbeidsgiver.setIdentifikator(lagIdentifikator(v.getOrgnr()));
            arbeidsgiver.setAdresse(hentAdresseFraDtoAdresse(v.getAdresse()));
            arbeidsgiver.setNavn(v.getNavn());

            arbeidsgiverList.add(arbeidsgiver);
        }

        selvstendig.setArbeidsgiver(arbeidsgiverList);

        return selvstendig;
    }

    default String formaterDato(LocalDate dato) {
        return Konstanter.dateTimeFormatter.format(dato);
    }

    default Adresse hentAdresseFraDtoAdresse(no.nav.melosys.eessi.controller.dto.Adresse sAdresse) {
        var adresse = new Adresse();
        adresse.setGate(sAdresse.getGateadresse());
        adresse.setPostnummer(sAdresse.getPostnr());
        adresse.setBy(sAdresse.getPoststed());
        adresse.setLand(LandkodeMapper.getLandkodeIso2(sAdresse.getLand()));
        adresse.setBygning(null);
        adresse.setRegion(sAdresse.getRegion());

        if (!StringUtils.hasText(adresse.getBy()) || !StringUtils.hasText(adresse.getLand())) {
            throw new MappingException("Felter 'poststed' og 'land' er påkrevd for adresser");
        }

        return adresse;
    }

    default List<Identifikator> lagIdentifikator(String orgnr) {
        if (!StringUtils.hasText(orgnr)) {
            return Collections.emptyList();
        }

        var orgNr = new Identifikator();
        orgNr.setId(orgnr);
        orgNr.setType("registrering");
        return List.of(orgNr);
    }

    default Periode mapTilPeriodeDto(Lovvalgsperiode lovvalgsperiode) {
        var periode = new Periode();

        if (lovvalgsperiode.getFom() != null) {
            if (lovvalgsperiode.getTom() != null) {
                var fastperiode = new Fastperiode();
                fastperiode.setStartdato(formaterDato(lovvalgsperiode.getFom()));
                fastperiode.setSluttdato(formaterDato(lovvalgsperiode.getTom()));
                periode.setFastperiode(fastperiode);
            } else {
                var aapenPeriode = new AapenPeriode();
                aapenPeriode.setStartdato(formaterDato(lovvalgsperiode.getFom()));
                periode.setAapenperiode(aapenPeriode);
            }
        } else {
            return null;
        }

        return periode;
    }

    default String landkodeIso2EllerNull(String iso3) {
        if (iso3 == null) {
            return null;
        } else if (iso3.length() == 2) {
            return iso3;
        } else {
            return LandkodeMapper.getLandkodeIso2(iso3);
        }
    }
}
