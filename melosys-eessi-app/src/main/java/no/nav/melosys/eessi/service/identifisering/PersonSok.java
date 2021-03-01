package no.nav.melosys.eessi.service.identifisering;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.exception.SecurityException;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.service.tps.personsok.PersonSokResponse;
import no.nav.melosys.eessi.service.tps.personsok.PersonsokKriterier;
import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.melosys.eessi.service.identifisering.PersonKontroller.harOverlappendeStatsborgerskap;
import static no.nav.melosys.eessi.service.identifisering.PersonKontroller.harSammeFoedselsdato;

@Slf4j
abstract class PersonSok {

    private final PersonFasade personFasade;

    @Autowired
    PersonSok(PersonFasade personFasade) {
        this.personFasade = personFasade;
    }

    PersonSokResultat søkEtterPerson(PersonsokKriterier personsokKriterier) {
        Collection<PersonSokResponse> personSøk = personFasade.soekEtterPerson(personsokKriterier);
        if (personSøk.isEmpty()) {
            return PersonSokResultat.ikkeIdentifisert(SoekBegrunnelse.INGEN_TREFF);
        } else if (personSøk.size() > 1) {
            return PersonSokResultat.ikkeIdentifisert(SoekBegrunnelse.FLERE_TREFF);
        }

        String ident = personSøk.iterator().next().getIdent();
        return vurderPerson(ident, personsokKriterier);
    }

    PersonSokResultat vurderPerson(String ident, PersonsokKriterier personsokKriterier) {
        PersonModell person;

        try {
            person = personFasade.hentPerson(ident);
        } catch (SecurityException e) {
            throw new IntegrationException("Sikkerhetsfeil mot tps",e);
        } catch (NotFoundException e) {
            log.warn("Feil ved henting av person", e);
            return PersonSokResultat.ikkeIdentifisert(SoekBegrunnelse.FNR_IKKE_FUNNET);
        }

        SoekBegrunnelse begrunnelse = vurderPerson(person, personsokKriterier);
        return begrunnelse == SoekBegrunnelse.IDENTIFISERT
                ? PersonSokResultat.identifisert(ident)
                : PersonSokResultat.ikkeIdentifisert(begrunnelse);
    }

    private SoekBegrunnelse vurderPerson(PersonModell person, PersonsokKriterier personsokKriterier) {
        if (person.isErOpphørt()) {
            return SoekBegrunnelse.PERSON_OPPHORT;
        } else if (!harOverlappendeStatsborgerskap(person, personsokKriterier)) {
            return SoekBegrunnelse.FEIL_STATSBORGERSKAP;
        } else if (!harSammeFoedselsdato(person, personsokKriterier)) {
            return SoekBegrunnelse.FEIL_FOEDSELSDATO;
        }

        return SoekBegrunnelse.IDENTIFISERT;
    }
}
