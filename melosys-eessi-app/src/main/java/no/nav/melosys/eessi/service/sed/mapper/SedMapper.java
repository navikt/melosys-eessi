package no.nav.melosys.eessi.service.sed.mapper;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import no.nav.melosys.eessi.controller.dto.*;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.Constants;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Adresse;
import no.nav.melosys.eessi.models.sed.nav.Arbeidssted;
import no.nav.melosys.eessi.models.sed.nav.Bruker;
import no.nav.melosys.eessi.models.sed.nav.Periode;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import no.nav.melosys.eessi.service.sed.helpers.PostnummerMapper;
import org.springframework.util.StringUtils;

import static no.nav.melosys.eessi.models.sed.Constants.SED_G_VER;
import static no.nav.melosys.eessi.models.sed.Constants.SED_VER;

/**
 * Felles mapper-interface for alle typer SED. Mapper NAV-objektet i NAV-SED, som brukes av eux for
 * å plukke ut nødvendig informasjon for en angitt SED.
 */
public interface SedMapper {
    default SED mapTilSed(SedDataDto sedData) throws MappingException, NotFoundException {
        SED sed = new SED();

        sed.setNav(prefillNav(sedData));
        sed.setSedType(getSedType().name());
        sed.setSedGVer(SED_G_VER);
        sed.setSedVer(SED_VER);

        return sed;
    }

    SedType getSedType();

    default Nav prefillNav(SedDataDto sedData) throws MappingException, NotFoundException {
        Nav nav = new Nav();

        nav.setBruker(hentBruker(sedData));
        nav.setArbeidssted(hentArbeidssted(sedData));
        nav.setArbeidsgiver(hentArbeidsGiver(sedData.getArbeidsgivendeVirksomheter()));

        if (!sedData.getSelvstendigeVirksomheter().isEmpty()) {
            nav.setSelvstendig(hentSelvstendig(sedData));
        }

        return nav;
    }

    default Bruker hentBruker(SedDataDto sedDataDto) throws NotFoundException {
        Bruker bruker = new Bruker();

        bruker.setPerson(hentPerson(sedDataDto));
        bruker.setAdresse(hentAdresser(sedDataDto));
        setFamiliemedlemmer(sedDataDto, bruker);

        return bruker;
    }

    default Person hentPerson(SedDataDto sedData) throws NotFoundException {
        Person person = new Person();

        person.setFornavn(sedData.getBruker().getFornavn());
        person.setEtternavn(sedData.getBruker().getEtternavn());
        person.setFoedselsdato(formaterDato(sedData.getBruker().getFoedseldato()));
        person.setFoedested(null); //det antas at ikke trengs når NAV fyller ut.
        person.setKjoenn(sedData.getBruker().getKjoenn());

        Statsborgerskap statsborgerskap = new Statsborgerskap();
        statsborgerskap.setLand(LandkodeMapper.getLandkodeIso2(sedData.getBruker().getStatsborgerskap()));

        person.setStatsborgerskap(Collections.singletonList(statsborgerskap));

        person.setPin(hentPin(sedData));

        return person;
    }

    default List<Pin> hentPin(SedDataDto sedData) throws NotFoundException {
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

    default List<Adresse> hentAdresser(SedDataDto sedDataDto) throws NotFoundException {

        Adresse adresse = new Adresse();
        adresse.setBy(sedDataDto.getBostedsadresse().getPoststed());
        adresse.setPostnummer(sedDataDto.getBostedsadresse().getPostnr());
        adresse.setLand(LandkodeMapper.getLandkodeIso2(sedDataDto.getBostedsadresse().getLand()));
        adresse.setGate(sedDataDto.getBostedsadresse().getGateadresse());
        adresse.setRegion(sedDataDto.getBostedsadresse().getRegion());
        adresse.setType(sedDataDto.getBostedsadresse().getAdressetype().getAdressetypeRina());

        // ref: punkt 2.1.1 (A001) https://confluence.adeo.no/display/TEESSI/Mapping+av+lovvalgs+SED+til+Melosys+domenemodell
        if (sedDataDto.getBostedsadresse().getAdressetype() == Adressetype.BOSTEDSADRESSE) {
            if ("NO".equalsIgnoreCase(adresse.getLand())) {
                adresse.setType(Adressetype.BOSTEDSADRESSE.getAdressetypeRina());
            } else {
                adresse.setType(Adressetype.POSTADRESSE.getAdressetypeRina());
            }
        }

        return Collections.singletonList(adresse);
    }

    default void setFamiliemedlemmer(SedDataDto sedData, Bruker bruker) {

        //Splitter per nå navnet etter første mellomrom
        Optional<FamilieMedlem> optionalFar = sedData.getFamilieMedlem().stream()
                .filter(f -> f.getRelasjon().equalsIgnoreCase("FAR")).findFirst();

        if (optionalFar.isPresent()) {
            Far far = new Far();
            Person person = new Person();
            person.setEtternavnvedfoedsel(optionalFar.get().getEtternavn());
            person.setFornavn(optionalFar.get().getFornavn());

            far.setPerson(person);
            bruker.setFar(far);
        }

        Optional<FamilieMedlem> optionalMor = sedData.getFamilieMedlem().stream()
                .filter(f -> f.getRelasjon().equalsIgnoreCase("MOR")).findFirst();

        if (optionalMor.isPresent()) {
            Mor mor = new Mor();
            Person person = new Person();
            person.setEtternavnvedfoedsel(optionalMor.get().getEtternavn());
            person.setFornavn(optionalMor.get().getFornavn());

            mor.setPerson(person);
            bruker.setMor(mor);
        }
    }

    default List<Arbeidssted> hentArbeidssted(SedDataDto sedData) throws MappingException, NotFoundException {

        List<Arbeidssted> arbeidsstedList = Lists.newArrayList();

        for (no.nav.melosys.eessi.controller.dto.Arbeidssted arbStd : sedData.getArbeidssteder()) {
            Arbeidssted arbeidssted = new Arbeidssted();
            arbeidssted.setNavn(arbStd.getNavn());
            arbeidssted.setAdresse(hentAdresseFraDtoAdresse(arbStd.getAdresse()));
            arbeidssted.setHjemmebase(landkodeIso2EllerNull(arbStd.getHjemmebase()));

            if (arbStd.isFysisk()) {
                arbeidssted.setErikkefastadresse("nei");
            } else if (!StringUtils.isEmpty(arbeidssted.getHjemmebase())) {
                arbeidssted.setErikkefastadresse("ja");
            }

            arbeidsstedList.add(arbeidssted);
        }

        return arbeidsstedList;
    }

    default List<Arbeidsgiver> hentArbeidsGiver(List<Virksomhet> virksomhetList)
            throws MappingException, NotFoundException {

        List<Arbeidsgiver> arbeidsgiverList = Lists.newArrayList();

        for (Virksomhet virksomhet : virksomhetList) {
            Arbeidsgiver arbeidsgiver = new Arbeidsgiver();
            arbeidsgiver.setNavn(virksomhet.getNavn());
            arbeidsgiver.setAdresse(hentAdresseFraDtoAdresse(virksomhet.getAdresse()));

            Identifikator orgNr = new Identifikator();
            orgNr.setId(virksomhet.getOrgnr());
            orgNr.setType("registrering"); //organisasjonsindenttypekoder.properties i eux står typer

            arbeidsgiver.setIdentifikator(Collections.singletonList(orgNr));

            arbeidsgiverList.add(arbeidsgiver);
        }

        return arbeidsgiverList;
    }

    default Selvstendig hentSelvstendig(SedDataDto sedData) throws MappingException, NotFoundException {

        Selvstendig selvstendig = new Selvstendig();
        List<Arbeidsgiver> arbeidsgiverList = Lists.newArrayList();

        for (Virksomhet v : sedData.getSelvstendigeVirksomheter()) {
            Arbeidsgiver arbeidsgiver = new Arbeidsgiver();

            Identifikator orgNr = new Identifikator();
            orgNr.setId(v.getOrgnr());
            orgNr.setType(
                    "registrering"); //organisasjonsindenttypekoder.properties i eux står typer

            arbeidsgiver.setIdentifikator(Collections.singletonList(orgNr));
            arbeidsgiver.setAdresse(hentAdresseFraDtoAdresse(v.getAdresse()));
            arbeidsgiver.setNavn(v.getNavn());

            arbeidsgiverList.add(arbeidsgiver);
        }

        selvstendig.setArbeidsgiver(arbeidsgiverList);

        return selvstendig;
    }

    default String formaterDato(LocalDate dato) {
        return Constants.dateTimeFormatter.format(dato);
    }

    default Adresse hentAdresseFraDtoAdresse(no.nav.melosys.eessi.controller.dto.Adresse sAdresse)
            throws MappingException, NotFoundException {
        Adresse adresse = new Adresse();
        adresse.setGate(sAdresse.getGateadresse());
        adresse.setPostnummer(sAdresse.getPostnr());
        adresse.setBy(StringUtils.isEmpty(sAdresse.getPoststed()) && !StringUtils.isEmpty(sAdresse.getPostnr()) ?
                PostnummerMapper.getPoststed(sAdresse.getPostnr()) : sAdresse.getPoststed());
        adresse.setLand(LandkodeMapper.getLandkodeIso2(sAdresse.getLand()));
        adresse.setBygning(null);
        adresse.setRegion(sAdresse.getRegion());

        if (StringUtils.isEmpty(adresse.getBy()) || StringUtils.isEmpty(adresse.getLand())) {
            throw new MappingException("Felter 'poststed' og 'land' er påkrevd for adresser");
        }

        return adresse;
    }

    default Lovvalgsperiode getLovvalgsperiode(SedDataDto sedData) {
        if (sedData.getLovvalgsperioder().isEmpty()) {
            return null;
        }

        return Collections.max(sedData.getLovvalgsperioder(), Comparator.comparing(Lovvalgsperiode::getFom));
    }

    default Periode mapTilPeriodeDto(Lovvalgsperiode lovvalgsperiode) {
        Periode periode = new Periode();

        if (lovvalgsperiode.getFom() != null) {
            if (lovvalgsperiode.getTom() != null) {
                Fastperiode fastperiode = new Fastperiode();
                fastperiode.setStartdato(formaterDato(lovvalgsperiode.getFom()));
                fastperiode.setSluttdato(formaterDato(lovvalgsperiode.getTom()));
                periode.setFastperiode(fastperiode);
            } else {
                AapenPeriode aapenPeriode = new AapenPeriode();
                aapenPeriode.setStartdato(formaterDato(lovvalgsperiode.getFom()));
                periode.setAapenperiode(aapenPeriode);
            }
        } else {
            return null;
        }

        return periode;
    }

    default String landkodeIso2EllerNull(String iso3) throws NotFoundException {
        if (iso3 == null) {
            return null;
        } else if (iso3.length() == 2) {
            return iso3;
        } else {
            return LandkodeMapper.getLandkodeIso2(iso3);
        }
    }
}
