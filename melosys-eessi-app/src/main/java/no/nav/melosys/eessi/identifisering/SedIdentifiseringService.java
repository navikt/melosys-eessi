package no.nav.melosys.eessi.identifisering;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Person;
import no.nav.melosys.eessi.models.sed.nav.Pin;
import no.nav.melosys.eessi.service.personsok.PersonsokKriterier;
import no.nav.melosys.eessi.service.sak.SakService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.springframework.stereotype.Service;

import static no.nav.melosys.eessi.models.DatoUtils.tilLocalDate;

@Slf4j
@Service
class SedIdentifiseringService implements PersonIdentifisering {

    private final PersonSok personSøk;
    private final SaksrelasjonService saksrelasjonService;
    private final SakService sakService;
    private final PersonFasade personFasade;
    private final PersonSokMetrikker personSokMetrikker;

    SedIdentifiseringService(
            PersonSok personSøk,
            SaksrelasjonService saksrelasjonService,
            SakService sakService,
            PersonFasade personFasade,
            PersonSokMetrikker personSokMetrikker) {
        this.personSøk = personSøk;
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

        return vurderEllerSøkEtterPerson(personFraSed.get());
    }

    private Optional<String> vurderEllerSøkEtterPerson(Person sedPerson) {
        PersonsokKriterier søkeKriterier = PersonsokKriterier.builder()
                .fornavn(sedPerson.getFornavn())
                .etternavn(sedPerson.getEtternavn())
                .foedselsdato(tilLocalDate(sedPerson.getFoedselsdato()))
                .statsborgerskapISO2(sedPerson.hentStatsborgerksapsliste())
                .build();

        Optional<String> norskIdent = sedPerson.finnNorskPin()
                .map(Pin::getIdentifikator)
                .flatMap(FnrUtils::filtrerUtGyldigNorskIdent);

        if (norskIdent.isPresent()) {
            PersonSokResultat resultat = personSøk.vurderPerson(norskIdent.get(), søkeKriterier);
            if (resultat.personIdentifisert()) {
                personSokMetrikker.counter(resultat.getBegrunnelse());
                return norskIdent;
            }
        }

        PersonSokResultat resultat = personSøk.søkEtterPerson(søkeKriterier);
        personSokMetrikker.counter(resultat.getBegrunnelse());
        log.info("Resultat fra forsøk på identifisering av person: {}", resultat.getBegrunnelse());
        return Optional.ofNullable(resultat.getIdent());
    }
}
