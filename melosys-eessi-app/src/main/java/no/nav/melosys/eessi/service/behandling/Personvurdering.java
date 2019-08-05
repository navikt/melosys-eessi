package no.nav.melosys.eessi.service.behandling;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.stream.Stream;
import io.micrometer.core.instrument.util.StringUtils;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.exception.SecurityException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import no.nav.melosys.eessi.service.tps.TpsService;
import no.nav.melosys.eessi.service.tps.personsok.PersonsoekKriterier;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Personvurdering {

    private static final String UTGAATT_PERSON = "UTPE";
    private static final String UTGAATT_PERSON_ANNULLERT_TILGANG = "UTAN";

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
     * @return Norsk ident til person eller null dersom den ikke finnes.
     * @throws NotFoundException Dersom landkode oppgitt er ugyldig
     */
    public String hentNorskIdent(SedHendelse sedMottatt, SED sed) throws NotFoundException {

        String ident = sedMottatt.getNavBruker();
        if (StringUtils.isEmpty(ident)) { // Ingen norsk ident oppgitt
            ident = soekOgVurderPerson(sed);
        } else {
            ident = hentOgVurderPerson(ident, sed);
            if (StringUtils.isEmpty(ident)) { // Person ble ikke funnet med ident angitt i Sed. Prøver å søke etter person
                ident = soekOgVurderPerson(sed);
            }
        }

        return ident;
    }

    /**
     * Søker etter en person basert på navn og fødselsdato i sed.
     */
    private String soekOgVurderPerson(SED sed) {
        String ident = soekEtterPerson(sed);
        if (!StringUtils.isEmpty(ident)) {
            ident = hentOgVurderPerson(ident, sed);
        }

        return ident;
    }

    private String hentOgVurderPerson(String ident, SED sed) {
        try {
            Person person = tpsService.hentPerson(ident);
            if (erOpphoert(person) || !harSammeStatsborgerskap(person, sed) || !harSammeFoedselsdato(person, sed)) {
                return null;
            }
        } catch (SecurityException | NotFoundException e) {
            return null;
        }

        return ident;
    }

    /**
     * Søker etter person i TPS basert på fornavn, etternavn og fødselsdato.
     *
     * @param sed SED som inneholder person med navn og fødselsdato
     * @return fødselsnummer/d-nummer for person, null hvis ikke funnet
     */
    private String soekEtterPerson(SED sed) {
        no.nav.melosys.eessi.models.sed.nav.Person sedPerson = sed.getNav().getBruker().getPerson();
        LocalDate foedselsdato = LocalDate.parse(sedPerson.getFoedselsdato(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String ident;
        try {
            ident = tpsService.soekEtterPerson(PersonsoekKriterier.builder()
                    .fornavn(sedPerson.getFornavn())
                    .etternavn(sedPerson.getEtternavn())
                    .foedselsdato(foedselsdato)
                    .build());
        } catch (NotFoundException e) {
            return null;
        }

        return ident;
    }

    public static boolean erOpphoert(Person person) {
        return Arrays.asList(UTGAATT_PERSON_ANNULLERT_TILGANG, UTGAATT_PERSON)
                .contains(person.getPersonstatus().getPersonstatus().getValue());
    }

    /**
     * Sjekker om person mottatt fra TPS har samme statsborgerskap som person i SED.
     *
     * @param person Person mottatt fra TPS
     * @param sed    SED mottatt fra kafka-kø
     * @return true dersom person og sed har samme statsborgerskap
     * @throws NotFoundException Kastes ved ukjent landkode.
     */
    private static boolean harSammeStatsborgerskap(Person person, SED sed) throws NotFoundException {
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
    private static boolean harSammeFoedselsdato(Person person, SED sed) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        Calendar tpsFoedselsdatoCalendar = person.getFoedselsdato().getFoedselsdato().toGregorianCalendar();
        dateFormatter.setTimeZone(tpsFoedselsdatoCalendar.getTimeZone());

        String tpsFoedselsdato = dateFormatter.format(tpsFoedselsdatoCalendar.getTime());
        String sedFoedselsdato = sed.getNav().getBruker().getPerson().getFoedselsdato();

        return tpsFoedselsdato.equalsIgnoreCase(sedFoedselsdato);
    }
}
