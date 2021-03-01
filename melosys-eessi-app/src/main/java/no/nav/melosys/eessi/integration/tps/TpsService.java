package no.nav.melosys.eessi.integration.tps;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.tps.aktoer.AktoerConsumer;
import no.nav.melosys.eessi.integration.tps.person.PersonConsumer;
import no.nav.melosys.eessi.integration.tps.personsok.PersonsokConsumer;
import no.nav.melosys.eessi.metrikker.PersonSokMetrikker;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.exception.SecurityException;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import no.nav.melosys.eessi.service.tps.personsok.PersonSokResponse;
import no.nav.melosys.eessi.service.tps.personsok.PersonsokKriterier;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonFault;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonFault1;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.PersonFilter;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.Soekekriterie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import static no.nav.melosys.eessi.models.DatoUtils.tilLocalDate;

@Slf4j
@Service
@Primary
public class TpsService implements PersonFasade {

    private final PersonConsumer personConsumer;
    private final AktoerConsumer aktoerConsumer;
    private final PersonsokConsumer personsokConsumer;
    private final PersonSokMetrikker personSokMetrikker;

    private static final String UTGAATT_PERSON = "UTPE";
    private static final String UTGAATT_PERSON_ANNULLERT_TILGANG = "UTAN";

    @Autowired
    public TpsService(PersonConsumer personConsumer,
                      AktoerConsumer aktoerConsumer,
                      PersonsokConsumer personsokConsumer,
                      PersonSokMetrikker personSokMetrikker) {
        this.personConsumer = personConsumer;
        this.aktoerConsumer = aktoerConsumer;
        this.personsokConsumer = personsokConsumer;
        this.personSokMetrikker = personSokMetrikker;
    }

    @Override
    public PersonModell hentPerson(String ident) {
        var res =
                hentPerson(
                        new HentPersonRequest()
                                .withAktoer(
                                        new PersonIdent().withIdent(
                                                new NorskIdent().withIdent(ident)
                                        )
                                )
                );

        return PersonModell.builder()
                .ident(ident)
                .fornavn(res.getPersonnavn().getFornavn())
                .etternavn(res.getPersonnavn().getEtternavn())
                .fødselsdato(tilLocalDate(res.getFoedselsdato().getFoedselsdato()))
                .statsborgerskapLandkodeISO2(
                        Collections.singleton(LandkodeMapper.getLandkodeIso2(res.getStatsborgerskap().getLand().getValue()))
                )
                .erOpphørt(
                        Arrays.asList(UTGAATT_PERSON_ANNULLERT_TILGANG, UTGAATT_PERSON)
                                .contains(res.getPersonstatus().getPersonstatus().getValue())
                )
                .build();
    }

    private Person hentPerson(HentPersonRequest request) {
        HentPersonResponse response;
        try {
            log.info("Henter person fra tps");
            response = personConsumer.hentPerson(request);
        } catch (HentPersonSikkerhetsbegrensning hentPersonSikkerhetsbegrensning) {
            throw new SecurityException("Ikke tilstrekkelig autentisering mot TPS", hentPersonSikkerhetsbegrensning);
        } catch (HentPersonPersonIkkeFunnet hentPersonPersonIkkeFunnet) {
            throw new NotFoundException("Person ikke funnet", hentPersonPersonIkkeFunnet);
        }

        return response.getPerson();
    }

    @Override
    public String hentAktoerId(String ident) {
        return aktoerConsumer.hentAktoerId(ident);
    }

    @Override
    public String hentNorskIdent(String aktoerID) {
        return aktoerConsumer.hentNorskIdent(aktoerID);
    }

    @Override
    public List<PersonSokResponse> soekEtterPerson(PersonsokKriterier personsokKriterier) {

        FinnPersonRequest request = new FinnPersonRequest();
        request.setPersonFilter(createPersonFilter(personsokKriterier));
        request.setSoekekriterie(createSoekekriterie(personsokKriterier));

        FinnPersonResponse response;
        try {
            response = personsokConsumer.finnPerson(request);
        } catch (FinnPersonFault1 ugyldigInputEx) {
            throw new IntegrationException("Ugyldig input i søk", ugyldigInputEx);
        } catch (FinnPersonFault forMangeForekomsterEx) {
            log.info("For mange forekomster ved søk mot TPS. Returnerer tom liste");
            return Collections.emptyList();
        }

        personSokMetrikker.registrerAntallTreffTps(response.getTotaltAntallTreff());
        return mapTilInternRespons(response);
    }

    private PersonFilter createPersonFilter(PersonsokKriterier personsokKriterier) {
        PersonFilter personFilter = new PersonFilter();

        try {
            if (personsokKriterier.getFoedselsdato() != null) {
                personFilter.setFoedselsdatoFra(toXmlGregorianCalendar(personsokKriterier.getFoedselsdato()));
                personFilter.setFoedselsdatoTil(toXmlGregorianCalendar(personsokKriterier.getFoedselsdato()));
            }
        } catch (DatatypeConfigurationException e) {
            log.error("Feil ved henting av dato, melding {}", e.getMessage(), e);
        }

        return personFilter;
    }

    private Soekekriterie createSoekekriterie(PersonsokKriterier personsokKriterier) {
        Soekekriterie soekekriterie = new Soekekriterie();
        soekekriterie.setFornavn(personsokKriterier.getFornavn());
        soekekriterie.setEtternavn(personsokKriterier.getEtternavn());
        return soekekriterie;
    }

    private XMLGregorianCalendar toXmlGregorianCalendar(LocalDate date) throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(GregorianCalendar.from(date.atStartOfDay(ZoneId.systemDefault())));
    }

    private static List<PersonSokResponse> mapTilInternRespons(FinnPersonResponse response) {
        return response.getPersonListe().stream()
                .map(p -> p.getIdent().getIdent())
                .map(PersonSokResponse::new)
                .collect(Collectors.toList());
    }
}
