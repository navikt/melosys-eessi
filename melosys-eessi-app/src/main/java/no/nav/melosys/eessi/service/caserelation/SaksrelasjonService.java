package no.nav.melosys.eessi.service.caserelation;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.repository.FagsakRinasakKoblingRepository;
import org.springframework.stereotype.Service;

@Service
public class SaksrelasjonService {

    private final FagsakRinasakKoblingRepository fagsakRinasakKoblingRepository;

    public SaksrelasjonService(FagsakRinasakKoblingRepository fagsakRinasakKoblingRepository) {
        this.fagsakRinasakKoblingRepository = fagsakRinasakKoblingRepository;
    }

    @Transactional
    public FagsakRinasakKobling lagreKobling(Long gsakSaksnummer, String rinaSaksnummer, BucType bucType) {
        FagsakRinasakKobling fagsakRinasakKobling = new FagsakRinasakKobling();
        fagsakRinasakKobling.setRinaSaksnummer(rinaSaksnummer);
        fagsakRinasakKobling.setGsakSaksnummer(gsakSaksnummer);
        fagsakRinasakKobling.setBucType(bucType);
        return fagsakRinasakKoblingRepository.save(fagsakRinasakKobling);
    }

    @Transactional
    public Optional<FagsakRinasakKobling> finnVedRinaId(String rinaSaksnummer) {
        return fagsakRinasakKoblingRepository.findByRinaSaksnummer(rinaSaksnummer);
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

}
