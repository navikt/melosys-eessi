package no.nav.melosys.eessi.service.identifisering;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.exception.SecurityException;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.service.tps.personsok.PersonSoekResponse;
import no.nav.melosys.eessi.service.tps.personsok.PersonsoekKriterier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.service.identifisering.PersonKontroller.harSammeFoedselsdato;
import static no.nav.melosys.eessi.service.identifisering.PersonKontroller.harSammeStatsborgerskap;

@Slf4j
@Component
class PersonSok {

    private final PersonFasade personFasade;

    @Autowired
    public PersonSok(PersonFasade personFasade) {
        this.personFasade = personFasade;
    }

    PersonSokResultat søkPersonFraSed(PersonsoekKriterier personsoekKriterier) {
        Collection<PersonSoekResponse> personSøk = personFasade.soekEtterPerson(personsoekKriterier);
        if (personSøk.isEmpty()) {
            return PersonSokResultat.ikkeIdentifisert(SoekBegrunnelse.INGEN_TREFF);
        } else if (personSøk.size() > 1) {
            return PersonSokResultat.ikkeIdentifisert(SoekBegrunnelse.FLERE_TREFF);
        }

        String ident = personSøk.iterator().next().getIdent();
        return vurderPerson(ident, personsoekKriterier);
    }

    PersonSokResultat vurderPerson(String ident, PersonsoekKriterier personsoekKriterier) {
        PersonModell person;

        try {
            person = personFasade.hentPerson(ident);
        } catch (SecurityException e) {
            throw new IntegrationException("Sikkerhetsfeil mot tps",e);
        } catch (NotFoundException e) {
            log.warn("Feil ved henting av person fra tps", e);
            return PersonSokResultat.ikkeIdentifisert(SoekBegrunnelse.FNR_IKKE_FUNNET);
        }

        SoekBegrunnelse begrunnelse = vurderPerson(person, personsoekKriterier);
        return begrunnelse == SoekBegrunnelse.IDENTIFISERT
                ? PersonSokResultat.identifisert(ident)
                : PersonSokResultat.ikkeIdentifisert(begrunnelse);
    }

    private SoekBegrunnelse vurderPerson(PersonModell person, PersonsoekKriterier personsoekKriterier) {
        if (person.isErOpphørt()) {
            return SoekBegrunnelse.PERSON_OPPHORT;
        } else if (!harSammeStatsborgerskap(person, personsoekKriterier)) {
            return SoekBegrunnelse.FEIL_STATSBORGERSKAP;
        } else if (!harSammeFoedselsdato(person, personsoekKriterier)) {
            return SoekBegrunnelse.FEIL_FOEDSELSDATO;
        }

        return SoekBegrunnelse.IDENTIFISERT;
    }
}
