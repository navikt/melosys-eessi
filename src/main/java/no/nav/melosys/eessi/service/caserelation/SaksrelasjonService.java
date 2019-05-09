package no.nav.melosys.eessi.service.caserelation;

import java.util.Optional;
import javax.transaction.Transactional;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakKobling;
import no.nav.melosys.eessi.models.RinasakKobling;
import no.nav.melosys.eessi.repository.FagsakKoblingRepository;
import no.nav.melosys.eessi.repository.RinasakKoblingRepository;
import org.springframework.stereotype.Service;

@Service
public class SaksrelasjonService {

    private final FagsakKoblingRepository fagsakKoblingRepository;
    private final RinasakKoblingRepository rinasakKoblingRepository;

    public SaksrelasjonService(FagsakKoblingRepository fagsakKoblingRepository,
            RinasakKoblingRepository rinasakKoblingRepository) {
        this.fagsakKoblingRepository = fagsakKoblingRepository;
        this.rinasakKoblingRepository = rinasakKoblingRepository;
    }

    @Transactional
    public FagsakKobling lagreKobling(Long gsakSaksnummer, String rinaId, BucType bucType) {
        Optional<FagsakKobling> optionalFagsakKobling = fagsakKoblingRepository.findByGsakSaksnummer(gsakSaksnummer);
        FagsakKobling fagsakKobling;

        if (optionalFagsakKobling.isPresent()) {

            fagsakKobling = optionalFagsakKobling.get();

            RinasakKobling rinasakKobling = new RinasakKobling();
            rinasakKobling.setRinaId(rinaId);
            rinasakKobling.setBucType(bucType);
            rinasakKobling.setFagsakKobling(fagsakKobling);

            return rinasakKoblingRepository.save(rinasakKobling).getFagsakKobling();

        } else {

            fagsakKobling = new FagsakKobling();
            fagsakKobling.setGsakSaksnummer(gsakSaksnummer);
            fagsakKobling = fagsakKoblingRepository.save(fagsakKobling);

            RinasakKobling rinasakKobling = new RinasakKobling();
            rinasakKobling.setRinaId(rinaId);
            rinasakKobling.setFagsakKobling(fagsakKobling);
            rinasakKobling.setBucType(bucType);
            return rinasakKoblingRepository.save(rinasakKobling).getFagsakKobling();
        }
    }

    @Transactional
    public Optional<RinasakKobling> finnVedRinaId(String rinaId) {
        return rinasakKoblingRepository.findByRinaId(rinaId);
    }

    @Transactional
    public void slettRinaId(String rinaId) {
        rinasakKoblingRepository.deleteByRinaId(rinaId);
    }

}
