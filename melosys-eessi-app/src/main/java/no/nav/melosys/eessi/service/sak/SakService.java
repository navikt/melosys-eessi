package no.nav.melosys.eessi.service.sak;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.integration.sak.SakConsumer;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SakService {

    private final SakConsumer sakConsumer;
    private final SaksrelasjonService saksrelasjonService;

    @Autowired
    public SakService(SakConsumer sakConsumer, SaksrelasjonService saksrelasjonService) {
        this.sakConsumer = sakConsumer;
        this.saksrelasjonService = saksrelasjonService;
    }

    public Sak hentsak(Long id) {
        return sakConsumer.getSak(Long.toString(id));
    }

    public Optional<Sak> finnSakForRinaSaksnummer(String rinaSaksnummer) {
        Optional<String> saksnummer = saksrelasjonService.søkEtterSaksnummerFraRinaSaksnummer(rinaSaksnummer)
                .map(Object::toString);

        if (saksnummer.isPresent()) {
            return Optional.of(sakConsumer.getSak(saksnummer.get()));
        }

        return Optional.empty();
    }
}
