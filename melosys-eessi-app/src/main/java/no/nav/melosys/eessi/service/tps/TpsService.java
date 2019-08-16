package no.nav.melosys.eessi.service.tps;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.tps.aktoer.AktoerConsumer;
import no.nav.melosys.eessi.integration.tps.person.PersonConsumer;
import no.nav.melosys.eessi.integration.tps.personsok.PersonsokConsumer;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.exception.SecurityException;
import no.nav.melosys.eessi.service.tps.personsok.PersonSoekResponse;
import no.nav.melosys.eessi.service.tps.personsok.PersonsoekKriterier;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonForMangeForekomster;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonUgyldigInput;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Personnavn;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.PersonFilter;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.Soekekriterie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class TpsService {

    private final PersonConsumer personConsumer;
    private final AktoerConsumer aktoerConsumer;
    private final PersonsokConsumer personsokConsumer;

    @Autowired
    public TpsService(PersonConsumer personConsumer,
                      AktoerConsumer aktoerConsumer,
                      PersonsokConsumer personsokConsumer) {
        this.personConsumer = personConsumer;
        this.aktoerConsumer = aktoerConsumer;
        this.personsokConsumer = personsokConsumer;
    }

    public Person hentPerson(String ident) throws SecurityException, NotFoundException {
        HentPersonRequest request = new HentPersonRequest()
                .withAktoer(new AktoerId()
                        .withAktoerId(ident));

        return hentPerson(request);
    }

    public Person hentPersonMedAdresse(String ident) throws SecurityException, NotFoundException {
        HentPersonRequest request = new HentPersonRequest()
                .withInformasjonsbehov(Informasjonsbehov.ADRESSE)
                .withAktoer(new AktoerId()
                        .withAktoerId(ident));

        return hentPerson(request);
    }

    private Person hentPerson(HentPersonRequest request) throws SecurityException, NotFoundException {
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

    public String hentAktoerId(String ident) throws NotFoundException {
        return aktoerConsumer.getAktoerId(ident);
    }

    public List<PersonSoekResponse> soekEtterPerson(PersonsoekKriterier personsoekKriterier) throws NotFoundException {

        FinnPersonRequest request = new FinnPersonRequest();
        request.setPersonFilter(createPersonFilter(personsoekKriterier));
        request.setSoekekriterie(createSoekekriterie(personsoekKriterier));

        FinnPersonResponse response;
        try {
            response = personsokConsumer.finnPerson(request);
        } catch (FinnPersonUgyldigInput finnPersonUgyldigInput) {
            throw new NotFoundException("Ugyldig input i søk", finnPersonUgyldigInput);
        } catch (FinnPersonForMangeForekomster finnPersonForMangeForekomster) {
            throw new NotFoundException("For mange forekomster funnet", finnPersonForMangeForekomster);
        }

        return mapTilInternRespons(response);
    }

    private PersonFilter createPersonFilter(PersonsoekKriterier personsoekKriterier) {
        PersonFilter personFilter = new PersonFilter();

        try {
            if (personsoekKriterier.getFoedselsdato() != null) {
                personFilter.setFoedselsdatoFra(toXmlGregorianCalendar(personsoekKriterier.getFoedselsdato()));
                personFilter.setFoedselsdatoTil(toXmlGregorianCalendar(personsoekKriterier.getFoedselsdato()));
            }
        } catch (DatatypeConfigurationException e) {
            log.error("Feil ved henting av dato, melding {}", e.getMessage(), e);
        }

        return personFilter;
    }

    private Soekekriterie createSoekekriterie(PersonsoekKriterier personsoekKriterier) {
        Soekekriterie soekekriterie = new Soekekriterie();
        soekekriterie.setFornavn(personsoekKriterier.getFornavn());
        soekekriterie.setEtternavn(personsoekKriterier.getEtternavn());
        return soekekriterie;
    }

    private XMLGregorianCalendar toXmlGregorianCalendar(LocalDate date) throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(GregorianCalendar.from(date.atStartOfDay(ZoneId.systemDefault())));
    }

    private static List<PersonSoekResponse> mapTilInternRespons(FinnPersonResponse response) {
        return response.getPersonListe().stream()
                .map(TpsService::mapTilInternRespons)
                .collect(Collectors.toList());
    }

    private static PersonSoekResponse mapTilInternRespons(no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Person person) {
        PersonSoekResponse intern = new PersonSoekResponse();
        intern.setDiskresjonskode(person.getDiskresjonskode() != null ? person.getDiskresjonskode().getValue() : null);
        intern.setIdent(person.getIdent() != null ? person.getIdent().getIdent() : null);

        Personnavn personnavn = person.getPersonnavn();
        if (personnavn != null) {
            intern.setEtternavn(personnavn.getEtternavn());
            intern.setFornavn(personnavn.getFornavn());
            intern.setMellomnavn(personnavn.getMellomnavn());
            intern.setSammensattNavn(personnavn.getSammensattNavn());
        }

        if (person.getKjoenn() != null && person.getKjoenn().getKjoenn() != null) {
            intern.setKjoenn(mapKjoenn(person.getKjoenn().getKjoenn().getValue()));
        }

        return intern;
    }

    private static PersonSoekResponse.Kjoenn mapKjoenn(String kjoenn) {
        if (StringUtils.isEmpty(kjoenn)) {
            return null;
        }

        if ("M".equals(kjoenn)) {
            return PersonSoekResponse.Kjoenn.MANN;
        } else if ("K".equals(kjoenn)) {
            return PersonSoekResponse.Kjoenn.KVINNE;
        } else {
            return PersonSoekResponse.Kjoenn.UKJENT;
        }

    }
}
