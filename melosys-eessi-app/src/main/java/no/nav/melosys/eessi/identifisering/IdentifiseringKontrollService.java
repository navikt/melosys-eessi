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

    public IdentifiseringKontrollService(PersonFasade personFasade, EuxService euxService) {
        this.personFasade = personFasade;
        this.euxService = euxService;
    }

    public IdentifiseringsKontrollResultat kontrollerIdentifisertPerson(String aktørID, String rinaSaksnummer) {
        var buc = euxService.hentBuc(rinaSaksnummer);
        var dokumentID = buc.finnFørstMottatteSed()
            .orElseThrow(() -> new NoSuchElementException("Finner ikke første mottatte SED"))
            .getId();

        var sedPerson = euxService.hentSed(rinaSaksnummer, dokumentID).finnPerson()
            .orElseThrow(() -> new NoSuchElementException("Finner ingen person fra SED"));

        var identifisertPerson = personFasade.hentPerson(aktørID);


        return new IdentifiseringsKontrollResultat(kontrollerIdentifisering(identifisertPerson, sedPerson, buc.hentAvsenderLand()));
    }

    private Collection<IdentifiseringsKontrollBegrunnelse> kontrollerIdentifisering(PersonModell identifisertPerson, Person sedPerson, String avsenderLand) {
        List<IdentifiseringsKontrollBegrunnelse> begrunnelser = new ArrayList<>();

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

        return begrunnelser;
    }
}
