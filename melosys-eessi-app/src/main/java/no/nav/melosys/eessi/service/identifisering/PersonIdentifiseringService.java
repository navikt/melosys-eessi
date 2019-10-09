package no.nav.melosys.eessi.service.identifisering;

import java.util.Optional;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.metrikker.PersonSokMetrikker;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.sak.SakService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import no.nav.melosys.eessi.service.tps.TpsService;
import org.springframework.stereotype.Service;

@Service
public class PersonIdentifiseringService {

    private final PersonSok personSok;
    private final SaksrelasjonService saksrelasjonService;
    private final SakService sakService;
    private final TpsService tpsService;
    private final PersonSokMetrikker personSokMetrikker;

    public PersonIdentifiseringService(PersonSok personSok,
            SaksrelasjonService saksrelasjonService,
            SakService sakService, TpsService tpsService,
            PersonSokMetrikker personSokMetrikker) {
        this.personSok = personSok;
        this.saksrelasjonService = saksrelasjonService;
        this.sakService = sakService;
        this.tpsService = tpsService;
        this.personSokMetrikker = personSokMetrikker;
    }

    public Optional<String> identifiserPerson(SedHendelse sedHendelse, SED sed)
            throws IntegrationException, NotFoundException {
        Optional<FagsakRinasakKobling> eksisterendeSak = saksrelasjonService.finnVedRinaId(sedHendelse.getRinaSakId());

        if (eksisterendeSak.isPresent()) {
            String aktoerID = sakService.hentsak(eksisterendeSak.get().getGsakSaksnummer()).getAktoerId();
            return Optional.ofNullable(tpsService.hentNorskIdent(aktoerID));
        }

        Optional<String> ident = Optional.ofNullable(sedHendelse.getNavBruker());
        if (ident.isPresent()) {
            PersonSokResultat resultat = personSok.vurderPerson(ident.get(), sed);
            if (resultat.personIdentifisert()) {
                personSokMetrikker.counter(resultat.getBegrunnelse());
                return ident;
            }
        }

        PersonSokResultat resultat = personSok.søkPersonFraSed(sed);
        personSokMetrikker.counter(resultat.getBegrunnelse());
        return Optional.ofNullable(resultat.getIdent());
    }
}
