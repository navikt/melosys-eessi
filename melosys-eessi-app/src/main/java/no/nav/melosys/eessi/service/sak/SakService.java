package no.nav.melosys.eessi.service.sak;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreConsumer;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreDto;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.integration.sak.SakConsumer;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SakService {

    private final SakConsumer sakConsumer;
    private final SaksrelasjonService saksrelasjonService;
    private final CaseStoreConsumer caseStoreConsumer;

    @Autowired
    public SakService(SakConsumer sakConsumer, SaksrelasjonService saksrelasjonService,
            CaseStoreConsumer caseStoreConsumer) {
        this.sakConsumer = sakConsumer;
        this.saksrelasjonService = saksrelasjonService;
        this.caseStoreConsumer = caseStoreConsumer;
    }

    public Sak hentsak(Long id) throws IntegrationException {
        return sakConsumer.getSak(Long.toString(id));
    }

    public Optional<Sak> finnSakForRinaSaksnummer(String rinaSaksnummer) throws IntegrationException {
        Optional<String> saksnummer = søkEtterSaksnummer(rinaSaksnummer);

        if (saksnummer.isPresent()) {
            return Optional.of(sakConsumer.getSak(saksnummer.get()));
        }

        return Optional.empty();
    }

    private Optional<String> søkEtterSaksnummer(String rinaSaksnummer) throws IntegrationException {
        Optional<String> saksnummer = saksrelasjonService.finnVedRinaId(rinaSaksnummer)
                .map(FagsakRinasakKobling::getGsakSaksnummer).map(Object::toString);

        if (!saksnummer.isPresent()) {
            saksnummer = caseStoreConsumer.finnVedRinaSaksnummer(rinaSaksnummer)
                    .stream().findFirst().map(CaseStoreDto::getFagsaknummer);
        }
        return saksnummer;
    }
}
