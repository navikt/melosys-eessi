package no.nav.melosys.eessi.service.identifisering;

import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.metrikker.PersonSokMetrikker;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Person;
import no.nav.melosys.eessi.models.sed.nav.Pin;
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap;
import no.nav.melosys.eessi.service.sak.SakService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import no.nav.melosys.eessi.service.tps.personsok.PersonsoekKriterier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static no.nav.melosys.eessi.models.DatoUtils.tilLocalDate;

@Slf4j
@Service
public class PersonIdentifiseringService {

    private final PersonSok tpsPersonSok;
    private final PersonSok pdlPersonSok;
    private final SaksrelasjonService saksrelasjonService;
    private final SakService sakService;
    private final PersonFasade personFasade;
    private final PersonSokMetrikker personSokMetrikker;

    public PersonIdentifiseringService(
            @Qualifier("tps") PersonSok tpsPersonSok,
            @Qualifier("pdl") PersonSok pdlPersonSok,
            SaksrelasjonService saksrelasjonService,
            SakService sakService,
            PersonFasade personFasade,
            PersonSokMetrikker personSokMetrikker) {
        this.tpsPersonSok = tpsPersonSok;
        this.pdlPersonSok = pdlPersonSok;
        this.saksrelasjonService = saksrelasjonService;
        this.sakService = sakService;
        this.personFasade = personFasade;
        this.personSokMetrikker = personSokMetrikker;
    }

    public Optional<String> identifiserPerson(String rinaSaksnumer, SED sed) {
        Optional<FagsakRinasakKobling> eksisterendeSak = saksrelasjonService.finnVedRinaSaksnummer(rinaSaksnumer);

        if (eksisterendeSak.isPresent()) {
            String aktoerID = sakService.hentsak(eksisterendeSak.get().getGsakSaksnummer()).getAktoerId();
            return Optional.of(personFasade.hentNorskIdent(aktoerID));
        }

        Optional<Person> personFraSed = sed.finnPerson();
        if (personFraSed.isEmpty()) {
            personSokMetrikker.counter(SoekBegrunnelse.INGEN_PERSON_I_SED);
            return Optional.empty();
        }

        return vurderPersonFraSed(personFraSed.get());
    }

    private Optional<String> vurderPersonFraSed(Person person) {
        PersonsoekKriterier søkeKriterier = PersonsoekKriterier.builder()
                .fornavn(person.getFornavn())
                .etternavn(person.getEtternavn())
                .foedselsdato(tilLocalDate(person.getFoedselsdato()))
                .statsborgerskapISO2(person.getStatsborgerskap().stream().map(Statsborgerskap::getLand).collect(Collectors.toSet()))
                .build();

        Optional<String> norskIdent = person.finnNorskPin().map(Pin::getIdentifikator).map(String::trim);
        if (norskIdent.isPresent()) {
            PersonSokResultat resultat = tpsPersonSok.vurderPerson(norskIdent.get(), søkeKriterier);
            if (resultat.personIdentifisert()) {
                personSokMetrikker.counter(resultat.getBegrunnelse());
                return norskIdent;
            }
        }

        PersonSokResultat resultat = utførSøk(søkeKriterier);
        personSokMetrikker.counter(resultat.getBegrunnelse());
        log.info("Resultat fra forsøk på identifisering av person: {}", resultat.getBegrunnelse());
        return Optional.ofNullable(resultat.getIdent());
    }

    private PersonSokResultat utførSøk(PersonsoekKriterier søkekriterier) {
        PersonSokResultat resultatTps = tpsPersonSok.søkEtterPerson(søkekriterier);

        try {
            personSokMetrikker.registrerSammenligningPdlTps(resultatTps, pdlPersonSok.søkEtterPerson(søkekriterier));
        } catch (Exception e) {
            log.error("Feil ved personsøk mot PDL", e);
        }

        return resultatTps;
    }
}
