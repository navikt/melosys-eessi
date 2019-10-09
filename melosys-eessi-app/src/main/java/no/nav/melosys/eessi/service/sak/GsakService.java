package no.nav.melosys.eessi.service.sak;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.integration.sak.SakConsumer;
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
    public GsakService(SakConsumer sakConsumer, SaksrelasjonService saksrelasjonService) {
        this.sakConsumer = sakConsumer;
        this.saksrelasjonService = saksrelasjonService;
    }

    public Sak hentsak(Long id) throws IntegrationException {
        return sakConsumer.getSak(id);
    }

    public Optional<Sak> finnSakForRinaID(String rinaId) throws IntegrationException {
        Optional<Long> gsakId = saksrelasjonService.finnVedRinaId(rinaId)
                .map(FagsakRinasakKobling::getGsakSaksnummer);

        if (gsakId.isPresent()) {
            log.info("Henter gsak med id: {}", gsakId.get());
            return Optional.of(hentsak(gsakId.get()));
        }

        return Optional.empty();
    }
}
