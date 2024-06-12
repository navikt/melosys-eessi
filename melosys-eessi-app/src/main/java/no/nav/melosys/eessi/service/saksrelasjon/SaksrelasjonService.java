package no.nav.melosys.eessi.service.saksrelasjon;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.repository.FagsakRinasakKoblingRepository;
import no.nav.melosys.eessi.service.sak.ArkivsakService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class SaksrelasjonService {

    private final FagsakRinasakKoblingRepository fagsakRinasakKoblingRepository;
    private final ArkivsakService arkivsakService;

    @Transactional
    public FagsakRinasakKobling lagreKobling(Long gsakSaksnummer, String rinaSaksnummer, BucType bucType) {
        var fagsakRinasakKobling = new FagsakRinasakKobling();
        fagsakRinasakKobling.setRinaSaksnummer(rinaSaksnummer);
        fagsakRinasakKobling.setGsakSaksnummer(gsakSaksnummer);
        fagsakRinasakKobling.setBucType(bucType);

        return fagsakRinasakKoblingRepository.save(fagsakRinasakKobling);
    }

    @Transactional
    public void slettVedRinaId(String rinaSaksnummer) {
        fagsakRinasakKoblingRepository.deleteByRinaSaksnummer(rinaSaksnummer);
    }

    public List<FagsakRinasakKobling> finnVedGsakSaksnummer(Long gsakSaksnummer) {
        return fagsakRinasakKoblingRepository.findAllByGsakSaksnummer(gsakSaksnummer);
    }

    public List<FagsakRinasakKobling> finnVedGsakSaksnummerOgBucType(Long gsakSaksnummer, BucType bucType) {
        return fagsakRinasakKoblingRepository.findAllByGsakSaksnummerAndBucType(gsakSaksnummer, bucType);
    }

    /**
     * Finner melosys-sak tilknyttet rina-sak lagret i egen DB
     */
    public Optional<FagsakRinasakKobling> finnVedRinaSaksnummer(String rinaSaksnummer) {
        return fagsakRinasakKoblingRepository.findByRinaSaksnummer(rinaSaksnummer);
    }

    public Optional<Long> søkEtterSaksnummerFraRinaSaksnummer(String rinaSaksnummer) {
        return fagsakRinasakKoblingRepository.findByRinaSaksnummer(rinaSaksnummer)
            .map(FagsakRinasakKobling::getGsakSaksnummer);
    }

    public Optional<String> finnAktørIDTilhørendeRinasak(String rinaSaksnummer) {
        return finnArkivsakForRinaSaksnummer(rinaSaksnummer)
            .map(Sak::getAktoerId);
    }

    public Optional<Sak> finnArkivsakForRinaSaksnummer(String rinaSaksnummer) {
        return finnVedRinaSaksnummer(rinaSaksnummer)
            .map(FagsakRinasakKobling::getGsakSaksnummer)
            .map(arkivsakService::hentsak);
    }
}
