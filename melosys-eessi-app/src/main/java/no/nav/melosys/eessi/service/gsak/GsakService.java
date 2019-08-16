package no.nav.melosys.eessi.service.gsak;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.integration.gsak.SakConsumer;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GsakService {

    private final SakConsumer sakConsumer;
    private final SaksrelasjonService saksrelasjonService;

    @Autowired
    public GsakService(SakConsumer sakConsumer,
            SaksrelasjonService saksrelasjonService) {
        this.sakConsumer = sakConsumer;
        this.saksrelasjonService = saksrelasjonService;
    }

    public Sak hentsak(Long id) throws IntegrationException {
        return sakConsumer.getSak(id);
    }

    public Sak hentEllerOpprettSak(String rinaId, String aktoerId, BucType bucType) throws IntegrationException {
        Optional<Long> gsakId = saksrelasjonService.finnVedRinaId(rinaId)
                .map(FagsakRinasakKobling::getGsakSaksnummer);

        if (gsakId.isPresent()) {
            log.info("Henter gsak med id: {}", gsakId.get());
            return hentsak(gsakId.get());
        } else {
            log.info("Oppretter ny sak i gsak for rinaSak {}", rinaId);
            return opprettSak(rinaId, aktoerId, bucType);
        }
    }

    private Sak opprettSak(String rinaId, String aktoerId, BucType bucType) throws IntegrationException {
        Sak sak = sakConsumer.opprettSak(aktoerId);
        saksrelasjonService.lagreKobling(Long.parseLong(sak.getId()), rinaId, bucType);
        log.info("Sak i gsak med id {} ble opprettet for rinaSak {}", sak.getId(), rinaId);
        return sak;
    }
}
