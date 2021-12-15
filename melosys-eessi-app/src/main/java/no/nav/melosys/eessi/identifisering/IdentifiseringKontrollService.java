package no.nav.melosys.eessi.identifisering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.models.person.UtenlandskId;
import no.nav.melosys.eessi.models.sed.nav.Person;
import no.nav.melosys.eessi.service.eux.EuxService;
import org.springframework.stereotype.Service;

import static no.nav.melosys.eessi.models.DatoUtils.tilLocalDate;

@Service
public class IdentifiseringKontrollService {

    private final PersonFasade personFasade;
    private final EuxService euxService;
    private final PersonSokMetrikker personSokMetrikker;
    private final static int OVERSTYREKONTROLLVERSJON = 2;

    public IdentifiseringKontrollService(PersonFasade personFasade, EuxService euxService, PersonSokMetrikker personSokMetrikker) {
        this.personFasade = personFasade;
        this.euxService = euxService;
        this.personSokMetrikker = personSokMetrikker;
    }

    public IdentifiseringsKontrollResultat kontrollerIdentifisertPerson(String aktørId, String rinaSaksnummer, int versjon) {
        var buc = euxService.hentBuc(rinaSaksnummer);
        var dokumentID = buc.finnFørstMottatteSed()
            .orElseThrow(() -> new NoSuchElementException("Finner ikke første mottatte SED"))
            .getId();

        var sedPerson = euxService.hentSed(rinaSaksnummer, dokumentID).finnPerson()
            .orElseThrow(() -> new NoSuchElementException("Finner ingen person fra SED"));

        var identifisertPerson = personFasade.hentPerson(aktørId);


        return new IdentifiseringsKontrollResultat(kontrollerIdentifisering(identifisertPerson, sedPerson, buc.hentAvsenderLand(), versjon));
    }

    private Collection<IdentifiseringsKontrollBegrunnelse> kontrollerIdentifisering(PersonModell identifisertPerson, Person sedPerson, String avsenderLand, int oppgaveVersjon) {
        List<IdentifiseringsKontrollBegrunnelse> begrunnelser = new ArrayList<>();

        if (oppgaveVersjon > OVERSTYREKONTROLLVERSJON) {
            personSokMetrikker.counter(IdentifiseringsKontrollBegrunnelse.OVERSTYREKONTROLL);
            return begrunnelser;
        }

        if (!PersonKontroller.harSammeFoedselsdato(identifisertPerson, tilLocalDate(sedPerson.getFoedselsdato()))) {
            begrunnelser.add(IdentifiseringsKontrollBegrunnelse.FØDSELSDATO);
        }
        if (sedPerson.harStatsborgerskap(avsenderLand) && !PersonKontroller.harStatsborgerskap(identifisertPerson, avsenderLand)) {
            begrunnelser.add(IdentifiseringsKontrollBegrunnelse.STATSBORGERSKAP);
        }
        if (!PersonKontroller.harSammeKjønn(identifisertPerson, sedPerson)) {
            begrunnelser.add(IdentifiseringsKontrollBegrunnelse.KJØNN);
        }

        var utenlandskIdFraAvsenderISed = sedPerson.finnUtenlandskIdFraLand(avsenderLand)
            .map(pin -> new UtenlandskId(pin.getIdentifikator(), pin.getLand()));
        if (utenlandskIdFraAvsenderISed.isPresent() && !PersonKontroller.harUtenlandskID(identifisertPerson, utenlandskIdFraAvsenderISed.get())) {
            begrunnelser.add(IdentifiseringsKontrollBegrunnelse.UTENLANDSK_ID);
        }

        begrunnelser.forEach(personSokMetrikker::counter);
        return begrunnelser;
    }
}
