package no.nav.melosys.eessi.service.caserelation;

import java.util.Optional;
import javax.transaction.Transactional;
import no.nav.melosys.eessi.models.CaseRelation;
import no.nav.melosys.eessi.repository.CaseRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaseRelationService {

    private final CaseRelationRepository caseRelationRepository;

    @Autowired
    public CaseRelationService(CaseRelationRepository caseRelationRepository) {
        this.caseRelationRepository = caseRelationRepository;
    }

    public CaseRelation save(Long gsakSaksnummer, String rinaCaseId) {
        CaseRelation caseRelation = new CaseRelation();
        caseRelation.setRinaId(rinaCaseId);
        caseRelation.setGsakSaksnummer(gsakSaksnummer);
        caseRelationRepository.save(caseRelation);
        return save(caseRelation);
    }

    @Transactional
    public CaseRelation save(CaseRelation caseRelation) {
        return caseRelationRepository.save(caseRelation);
    }

    public Optional<CaseRelation> findByRinaId(String rinaId) {
        return caseRelationRepository.findByRinaId(rinaId);
    }

    public Optional<CaseRelation> findByGsakSaksnummer(Long gsakSaksnummer) {
        return caseRelationRepository.findByGsakSaksnummer(gsakSaksnummer);
    }
}
