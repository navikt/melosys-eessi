package no.nav.melosys.eessi.service.buc;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.BucinfoDto;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.caserelation.SaksrelasjonService;
import no.nav.melosys.eessi.service.eux.EuxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BucService {

    private final EuxService euxService;

    private final SaksrelasjonService saksrelasjonService;

    @Autowired
    public BucService(EuxService euxService, SaksrelasjonService saksrelasjonService) {
        this.euxService = euxService;
        this.saksrelasjonService = saksrelasjonService;
    }

    public List<BucinfoDto> hentBucer(Long gsakSaksnummer, String status) {
        return saksrelasjonService.finnVedGsakSaksnummer(gsakSaksnummer).stream()
                .map(FagsakRinasakKobling::getRinaSaksnummer)
                .map(this::hentBuc)
                .filter(Objects::nonNull)
                .map(buc -> BucinfoDto.av(buc, status, euxService.hentRinaUrlPrefix()))
                .collect(Collectors.toList());
    }

    private BUC hentBuc(String rinaSaksnummer) {
        try {
            return euxService.hentBuc(rinaSaksnummer);
        } catch (IntegrationException e) {
            log.error("Kunne ikke hente BUC {}", rinaSaksnummer, e);
            return null;
        }
    }
}
