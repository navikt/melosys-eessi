package no.nav.melosys.eessi.identifisering;

import lombok.extern.slf4j.Slf4j;
import no.finn.unleash.Unleash;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.service.personsok.PersonSokResponse;
import no.nav.melosys.eessi.service.personsok.PersonsokKriterier;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static no.nav.melosys.eessi.identifisering.PersonKontroller.harOverlappendeStatsborgerskap;
import static no.nav.melosys.eessi.identifisering.PersonKontroller.harSammeFoedselsdato;

@Slf4j
@Component
class PersonSok {

    private final PersonFasade personFasade;
    private final Unleash unleash;

    PersonSok(PersonFasade personFasade, Unleash unleash) {
        this.personFasade = personFasade;
        this.unleash = unleash;
    }

    PersonSokResultat søkEtterPerson(PersonsokKriterier personsokKriterier) {
        Collection<PersonSokResponse> personSøk = unleash.isEnabled("melosys.send_til_id_og_fordeling_dersom_ingen_folkeregisterident")
            ? personFasade.soekEtterPerson(personsokKriterier)
            : personFasade.soekEtterPersonGammel(personsokKriterier);
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
