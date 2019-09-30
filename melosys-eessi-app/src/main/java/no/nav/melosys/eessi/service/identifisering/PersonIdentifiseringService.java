package no.nav.melosys.eessi.service.identifisering;

import java.util.Optional;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.gsak.GsakService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import no.nav.melosys.eessi.service.tps.TpsService;
import org.springframework.stereotype.Service;

@Service
public class PersonIdentifiseringService {

    private final PersonsokSok personsokSok;
    private final SaksrelasjonService saksrelasjonService;
    private final GsakService gsakService;
    private final TpsService tpsService;

    public PersonIdentifiseringService(PersonsokSok personsokSok, SaksrelasjonService saksrelasjonService,
            GsakService gsakService, TpsService tpsService) {
        this.personsokSok = personsokSok;
        this.saksrelasjonService = saksrelasjonService;
        this.gsakService = gsakService;
        this.tpsService = tpsService;
    }

    public Optional<String> identifiserPerson(SedHendelse sedHendelse, SED sed)
            throws IntegrationException, NotFoundException {
        Optional<FagsakRinasakKobling> eksisterendeSak = saksrelasjonService.finnVedRinaId(sedHendelse.getRinaSakId());

        if (eksisterendeSak.isPresent()) {
            String aktoerID = gsakService.hentsak(eksisterendeSak.get().getGsakSaksnummer()).getAktoerId();
            return Optional.ofNullable(tpsService.hentNorskIdent(aktoerID));
        }

        return personsokSok.finnNorskIdent(sedHendelse, sed);
    }
}
