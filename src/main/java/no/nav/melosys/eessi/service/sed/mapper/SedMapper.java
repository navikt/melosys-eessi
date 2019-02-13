package no.nav.melosys.eessi.service.sed.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import com.google.common.collect.Lists;
import no.nav.melosys.eessi.controller.dto.FamilieMedlem;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.Virksomhet;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.SedType;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import no.nav.melosys.eessi.service.sed.helpers.PostnummerMapper;
import org.springframework.util.StringUtils;

/**
 * Felles mapper-interface for alle typer SED. Mapper NAV-objektet i NAV-SED, som brukes av eux for
 * å plukke ut nødvendig informasjon for en angitt SED.
 */
public interface SedMapper<T extends Medlemskap> {

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //Versjonen til SED'en. Generasjon og versjon (SED_G_VER.SED_VER = 4.1)
    String SED_G_VER = "4";
    String SED_VER = "1";

    // Hvis det skulle trenges noen spesifikke endringer av NAV-objektet for enkelte SED'er,
    // bør denne metoden overrides.
    default SED mapTilSed(SedDataDto sedData) throws MappingException, NotFoundException {
        SED sed = new SED();

        sed.setNav(prefillNav(sedData));
        sed.setSed(getSedType().name());
        sed.setSedGVer(SED_G_VER);
        sed.setSedVer(SED_VER);
        sed.setMedlemskap(getMedlemskap(sedData));

        return sed;
    }

    T getMedlemskap(SedDataDto sedData) throws MappingException, NotFoundException;

    SedType getSedType();

    default Nav prefillNav(SedDataDto sedData) throws MappingException, NotFoundException {
        Nav nav = new Nav();

        nav.setBruker(hentBruker(sedData));
        nav.setArbeidssted(hentArbeidssted(sedData));
        nav.setArbeidsgiver(hentArbeidsGiver(sedData.getArbeidsgivendeVirksomheter()));

        if (sedData.isEgenAnsatt() && !sedData.getSelvstendigeVirksomheter().isEmpty()) {
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

    default List<Pin> hentPin(SedDataDto sedData) {
        List<Pin> pins = Lists.newArrayList();

        pins.add(new Pin(
                sedData.getBruker().getFnr(), "NO",
                null)); //null settes for sektor per nå. Ikke påkrevd. Evt hardkode 'alle'

        sedData.getUtenlandskIdent().stream()
                .map(utenlandskIdent -> {
                    try {
                        return new Pin(utenlandskIdent.getIdent(),
                                LandkodeMapper.getLandkodeIso2(utenlandskIdent.getLandkode()), null);
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .forEachOrdered(pins::add);

        return pins;
    }

    default List<Adresse> hentAdresser(SedDataDto sedDataDto) throws NotFoundException {

        Adresse adresse = new Adresse();
        adresse.setBy(sedDataDto.getBostedsadresse().getPoststed());
        adresse.setPostnummer(sedDataDto.getBostedsadresse().getPostnr());
        adresse.setLand(LandkodeMapper.getLandkodeIso2(sedDataDto.getBostedsadresse().getLand()));
        adresse.setGate(sedDataDto.getBostedsadresse().getGateadresse());
        adresse.setType("bosted");

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
            if (arbStd.isFysisk()) {
                arbeidssted.setAdresse(hentAdresseFraDtoAdresse(arbStd.getAdresse()));
            } else {
                arbeidssted.setErikkefastadresse("ja");
                arbeidssted.setHjemmebase(
                        ""); //TODO: maritime/ikke fysisk arbeidssteder. holder med isFysisk sjekk? Trenger muligens felt hjemmebase
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
        return dateTimeFormatter.format(dato);
    }

    default Adresse hentAdresseFraDtoAdresse(no.nav.melosys.eessi.controller.dto.Adresse sAdresse)
            throws MappingException, NotFoundException {
        Adresse adresse = new Adresse();
        adresse.setGate(sAdresse.getGateadresse());
        adresse.setPostnummer(sAdresse.getPostnr());
        adresse.setBy(StringUtils.isEmpty(sAdresse.getPoststed()) ?
                PostnummerMapper.getPoststed(sAdresse.getPostnr()) : sAdresse.getPoststed());
        adresse.setLand(LandkodeMapper.getLandkodeIso2(sAdresse.getLand()));
        adresse.setBygning(null);

        if (StringUtils.isEmpty(adresse.getBy()) || StringUtils.isEmpty(adresse.getLand())) {
            throw new MappingException(
                    "Element 'poststed' and 'land' and  is required for all addresses");
        }

        return adresse;
    }
}
