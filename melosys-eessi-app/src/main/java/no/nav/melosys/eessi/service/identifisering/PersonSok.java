package no.nav.melosys.eessi.service.identifisering;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.exception.SecurityException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.tps.TpsService;
import no.nav.melosys.eessi.service.tps.personsok.PersonSoekResponse;
import no.nav.melosys.eessi.service.tps.personsok.PersonsoekKriterier;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.models.DatoUtils.tilLocalDate;
import static no.nav.melosys.eessi.service.identifisering.PersonKontroller.*;

@Slf4j
@Component
class PersonSok {

    private final TpsService tpsService;

    @Autowired
    public PersonSok(TpsService tpsService) {
        this.tpsService = tpsService;
    }

    PersonSokResultat søkPersonFraSed(SED sed) throws IntegrationException {
        List<PersonSoekResponse> personSøk = søkEtterPerson(sed);
        if (personSøk.isEmpty()) {
            return PersonSokResultat.ikkeIdentifisert(SoekBegrunnelse.INGEN_TREFF);
        } else if (personSøk.size() > 1) {
            return PersonSokResultat.ikkeIdentifisert(SoekBegrunnelse.FLERE_TREFF);
        }

        String ident = personSøk.get(0).getIdent();
        return vurderPerson(ident, sed);
    }

    PersonSokResultat vurderPerson(String ident, SED sed) throws IntegrationException {
        Person person;

        try {
            person = tpsService.hentPerson(ident);
        } catch (SecurityException e) {
            throw new IntegrationException("Sikkerhetsfeil mot tps",e);
        } catch (NotFoundException e) {
            log.warn("Feil ved henting av person fra tps", e);
            return PersonSokResultat.ikkeIdentifisert(SoekBegrunnelse.FNR_IKKE_FUNNET);
        }

        SoekBegrunnelse begrunnelse = vurderPerson(person, sed);
        return begrunnelse == SoekBegrunnelse.IDENTIFISERT
                ? PersonSokResultat.identifisert(ident)
                : PersonSokResultat.ikkeIdentifisert(begrunnelse);
    }

    private SoekBegrunnelse vurderPerson(Person person, SED sed) {
        if (erOpphoert(person)) {
            return SoekBegrunnelse.PERSON_OPPHORT;
        } else if (!harSammeStatsborgerskap(person, sed)) {
            return SoekBegrunnelse.FEIL_STATSBORGERSKAP;
        } else if (!harSammeFoedselsdato(person, sed)) {
            return SoekBegrunnelse.FEIL_FOEDSELSDATO;
        }

        return SoekBegrunnelse.IDENTIFISERT;
    }

    /**
     * Søker etter person i TPS basert på fornavn, etternavn og fødselsdato.
     *
     * @param sed SED som inneholder person med navn og fødselsdato
     * @return fødselsnummer/d-nummer for person, null hvis ikke funnet
     */
    private List<PersonSoekResponse> søkEtterPerson(SED sed) throws IntegrationException {
        no.nav.melosys.eessi.models.sed.nav.Person sedPerson = sed.getNav().getBruker().getPerson();
        LocalDate foedselsdato = tilLocalDate(sedPerson.getFoedselsdato());

        List<PersonSoekResponse> response;
        try {
            response = tpsService.soekEtterPerson(PersonsoekKriterier.builder()
                    .fornavn(sedPerson.getFornavn())
                    .etternavn(sedPerson.getEtternavn())
                    .foedselsdato(foedselsdato)
                    .build());
        } catch (NotFoundException e) {
            //Mappet fra FinnPersonForMangeForekomster-exception. OK med tom liste
            return Collections.emptyList();
        }

        return response;
    }
}
