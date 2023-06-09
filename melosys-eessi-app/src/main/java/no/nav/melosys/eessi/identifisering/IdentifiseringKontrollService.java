package no.nav.melosys.eessi.identifisering;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.models.person.UtenlandskId;
import no.nav.melosys.eessi.models.sed.nav.Person;
import no.nav.melosys.eessi.service.eux.EuxService;
import org.springframework.stereotype.Service;

import static no.nav.melosys.eessi.models.DatoUtils.tilLocalDate;

@Slf4j
@Service
@AllArgsConstructor
public class IdentifiseringKontrollService {

    private static final int MAKS_OPPGAVEVERSJON_UTEN_OVERSTYRING = 2; // 2 versjon tilsvarer 2 gang hos id og fordeling

    private final PersonFasade personFasade;
    private final EuxService euxService;
    private final PersonSokMetrikker personSokMetrikker;

    public IdentifiseringsKontrollResultat kontrollerIdentifisertPerson(String aktørId, String rinaSaksnummer, int oppgaveEndretVersjon) {
        var buc = euxService.hentBuc(rinaSaksnummer);
        String dokumentID;
        dokumentID = buc.finnFørstMottatteSed()
            .orElseThrow(() -> new NoSuchElementException("Finner ikke første mottatte SED"))
            .getId();

        var sedPerson = euxService.hentSed(rinaSaksnummer, dokumentID).finnPerson()
            .orElseThrow(() -> new NoSuchElementException("Finner ingen person fra SED"));

        var identifisertPerson = personFasade.hentPerson(aktørId);


        if (oppgaveEndretVersjon >= MAKS_OPPGAVEVERSJON_UTEN_OVERSTYRING) {
            personSokMetrikker.counter(IdentifiseringsKontrollBegrunnelse.OVERSTYREKONTROLL);
            return new IdentifiseringsKontrollResultat(Collections.emptyList());
        }

        return new IdentifiseringsKontrollResultat(kontrollerIdentifisering(identifisertPerson, sedPerson, buc.hentAvsenderLand()));
    }

    private Collection<IdentifiseringsKontrollBegrunnelse> kontrollerIdentifisering(PersonModell identifisertPerson, Person sedPerson, String avsenderLand) {
        List<IdentifiseringsKontrollBegrunnelse> begrunnelser = new ArrayList<>();

        if (!PersonKontroller.harSammeFoedselsdato(identifisertPerson, tilLocalDate(sedPerson.getFoedselsdato()))) {
            log.info("PDL og SED har forskjellig fødselsdato for personen");
            begrunnelser.add(IdentifiseringsKontrollBegrunnelse.FØDSELSDATO);
        }
        if ((sedPerson.harStatsborgerskap(avsenderLand) && !PersonKontroller.harStatsborgerskap(identifisertPerson, avsenderLand))
            || (!PersonKontroller.harStatsborgerskapIListe(identifisertPerson, sedPerson.hentStatsborgerksapsliste()))) {
            String statsborgerskapFraPDL = String.join(",", identifisertPerson.getStatsborgerskapLandkodeISO2());
            String statsborgerskapFraSED = String.join(",", sedPerson.hentStatsborgerksapsliste());
            log.info("PDL, SED eller Buc har forskjellig statsborgerskap, PDL: {}, SED: {}, Buc: {}", statsborgerskapFraPDL, statsborgerskapFraSED, avsenderLand);
            begrunnelser.add(IdentifiseringsKontrollBegrunnelse.STATSBORGERSKAP);
        }
        if (!PersonKontroller.harUkjentEllerSammeKjønn(identifisertPerson, sedPerson)) {
            log.info("PDL og SED har forskjellig kjønn for personen");
            begrunnelser.add(IdentifiseringsKontrollBegrunnelse.KJØNN);
        }

        var utenlandskIdFraAvsenderISed = sedPerson.finnUtenlandskIdFraLand(avsenderLand)
            .map(pin -> new UtenlandskId(pin.getIdentifikator(), pin.getLand()));
        if (utenlandskIdFraAvsenderISed.isPresent() && !PersonKontroller.harUtenlandskID(identifisertPerson, utenlandskIdFraAvsenderISed.get())) {
            log.info("PDL og SED har forskjellig utenlandsk ID for personen");
            begrunnelser.add(IdentifiseringsKontrollBegrunnelse.UTENLANDSK_ID);
        }

        begrunnelser.forEach(personSokMetrikker::counter);
        return begrunnelser;
    }
}
