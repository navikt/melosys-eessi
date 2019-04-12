package no.nav.melosys.eessi.service.behandling;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.stream.Stream;
import io.micrometer.core.instrument.util.StringUtils;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.exception.ValidationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import no.nav.melosys.eessi.service.tps.TpsService;
import no.nav.melosys.eessi.service.tps.personsok.PersonsoekKriterier;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Personvurdering {

    private final TpsService tpsService;

    @Autowired
    public Personvurdering(TpsService tpsService) {
        this.tpsService = tpsService;
    }

    /**
     * Vurderer om personen er kjent fra før (i TPS) ut fra følgende opplysninger:
     * - Fødselsdato
     * - Statsborgerskap
     * <p>
     * Dersom ident (norsk fnr/dnr) ikke er oppgitt i sedMottatt søkes det mot TPS basert på navn og fødselsdato.
     * Dersom ident oppgitt i sedMottatt ikke blir funnet i TPS, gjøres det et søk mot TPS.
     * Dersom søket mot TPS bare inneholder 1 treff, blir denne personen hentet og vurdert.
     *
     * @param sedMottatt Objekt som blir mottatt fra kafka-køen
     * @param sed        SED-dokument hentet fra eux
     * @throws NotFoundException   Dersom person ikke blir funnet i TPS ved enten søk eller oppslag på fnr
     * @throws ValidationException Dersom opplysninger om person hentet fra TPS ikke stemmer overens med opplysninger
     *                             i SED eller dersom tpsService ikke klarer å hente person fra TPS
     */
    public void vurderPerson(SedHendelse sedMottatt, SED sed) throws NotFoundException, ValidationException {

        String ident = sedMottatt.getNavBruker();
        if (StringUtils.isEmpty(ident)) { // Ingen norsk ident oppgitt
            soekOgVurderPerson(sedMottatt, sed);
        } else {
            try {
                hentOgVurderPerson(ident, sed);
            } catch (NotFoundException e) { // Person ble ikke funnet med ident angitt i Sed. Prøver å søke etter person
                soekOgVurderPerson(sedMottatt, sed);
            }
        }
    }

    /**
     * Søker etter en person basert på navn og fødselsdato i sed. Dersom den finnes og blir validert
     * så oppdateres sedHendelse.navBruker til den nye (rette) identen.
     */
    private void soekOgVurderPerson(SedHendelse sedMottatt, SED sed) throws NotFoundException, ValidationException {
        String ident = soekEtterPerson(sed);
        hentOgVurderPerson(ident, sed);
        sedMottatt.setNavBruker(ident);
    }

    private void hentOgVurderPerson(String ident, SED sed) throws NotFoundException, ValidationException {
        try {
            Person person = tpsService.hentPerson(ident);
            if (!harSammeStatsborgerskap(person, sed) || !harSammeFoedselsdato(person, sed)) {
                throw new ValidationException("Person kunne ikke vurderes, person har ikke samme statsborgerskap eller fødselsdato i TPS");
            }
        } catch (HentPersonPersonIkkeFunnet hentPersonPersonIkkeFunnet) {
            throw new NotFoundException("Person ble ikke funnet i TPS: " + hentPersonPersonIkkeFunnet.getMessage());
        } catch (HentPersonSikkerhetsbegrensning hentPersonSikkerhetsbegrensning) {
            throw new ValidationException("Kunne ikke hente person fra TPS: " + hentPersonSikkerhetsbegrensning.getMessage());
        }
    }

    /**
     * Søker etter person i TPS basert på fornavn, etternavn og fødselsdato.
     *
     * @param sed SED som inneholder person med navn og fødselsdato
     * @return fødselsnummer/d-nummer for person
     * @throws NotFoundException Kastes dersom det mottas ugyldige søkedata
     *                           eller dersom antall treff på person er ulik 1
     */
    private String soekEtterPerson(SED sed) throws NotFoundException {
        no.nav.melosys.eessi.models.sed.nav.Person sedPerson = sed.getNav().getBruker().getPerson();
        LocalDate foedselsdato = LocalDate.parse(sedPerson.getFoedselsdato(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String ident = tpsService.soekEtterPerson(PersonsoekKriterier.builder()
                .fornavn(sedPerson.getFornavn())
                .etternavn(sedPerson.getEtternavn())
                .foedselsdato(foedselsdato)
                .build());

        if (StringUtils.isEmpty(ident)) {
            throw new NotFoundException("Søk etter person gav ingen treff");
        }

        return ident;
    }

    /**
     * Sjekker om person mottatt fra TPS har samme statsborgerskap som person i SED.
     *
     * @param person Person mottatt fra TPS
     * @param sed    SED mottatt fra kafka-kø
     * @return true dersom person og sed har samme statsborgerskap
     */
    private boolean harSammeStatsborgerskap(Person person, SED sed) throws NotFoundException {
        String tpsStatsborgerskap = LandkodeMapper.getLandkodeIso2(person.getStatsborgerskap().getLand().getValue());
        Stream<String> sedStatsborgerskap = sed.getNav().getBruker().getPerson().getStatsborgerskap()
                .stream().map(Statsborgerskap::getLand);

        return sedStatsborgerskap.anyMatch(tpsStatsborgerskap::equalsIgnoreCase);
    }

    /**
     * Sjekker om person mottatt fra TPS har samme fødselsdato som person i SED.
     *
     * @param person Person mottatt fra TPS
     * @param sed    SED mottatt fra kafka-kø
     * @return true dersom person og sed har samme fødselsdato
     */
    private boolean harSammeFoedselsdato(Person person, SED sed) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        Calendar tpsFoedselsdatoCalendar = person.getFoedselsdato().getFoedselsdato().toGregorianCalendar();
        dateFormatter.setTimeZone(tpsFoedselsdatoCalendar.getTimeZone());

        String tpsFoedselsdato = dateFormatter.format(tpsFoedselsdatoCalendar.getTime());
        String sedFoedselsdato = sed.getNav().getBruker().getPerson().getFoedselsdato();

        return tpsFoedselsdato.equalsIgnoreCase(sedFoedselsdato);
    }
}
