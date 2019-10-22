package no.nav.melosys.eessi.service.saksrelasjon;

import java.util.List;
import java.util.Optional;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreConsumer;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreDto;
import no.nav.melosys.eessi.integration.sak.SakConsumer;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.repository.FagsakRinasakKoblingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaksrelasjonService {

    private final FagsakRinasakKoblingRepository fagsakRinasakKoblingRepository;
    private final CaseStoreConsumer caseStoreConsumer;
    private final SakConsumer sakConsumer;


    public SaksrelasjonService(FagsakRinasakKoblingRepository fagsakRinasakKoblingRepository,
            CaseStoreConsumer caseStoreConsumer, SakConsumer sakConsumer) {
        this.fagsakRinasakKoblingRepository = fagsakRinasakKoblingRepository;
        this.caseStoreConsumer = caseStoreConsumer;
        this.sakConsumer = sakConsumer;
    }

    @Transactional
    public FagsakRinasakKobling lagreKobling(Long gsakSaksnummer, String rinaSaksnummer, BucType bucType)
            throws IntegrationException {
        FagsakRinasakKobling fagsakRinasakKobling = new FagsakRinasakKobling();
        fagsakRinasakKobling.setRinaSaksnummer(rinaSaksnummer);
        fagsakRinasakKobling.setGsakSaksnummer(gsakSaksnummer);
        fagsakRinasakKobling.setBucType(bucType);
        fagsakRinasakKobling = fagsakRinasakKoblingRepository.save(fagsakRinasakKobling);

        if (!bucType.erLovvalgBuc()) {
            oppdaterEllerLagreIEuxCaseStore(gsakSaksnummer, rinaSaksnummer);
        }

        return fagsakRinasakKobling;
    }

    private void oppdaterEllerLagreIEuxCaseStore(Long gsakSaksnummer, String rinaSaksnummer) throws IntegrationException {
        Optional<CaseStoreDto> caseStoreDto = caseStoreConsumer.finnVedRinaSaksnummer(rinaSaksnummer)
                .stream().findFirst();
        if (caseStoreDto.isPresent()) {
            String tema = sakConsumer.getSak(gsakSaksnummer.toString()).getTema();
            CaseStoreDto dto = caseStoreDto.get();
            dto.setTema(tema);
            dto.setFagsaknummer(gsakSaksnummer.toString());
            caseStoreConsumer.lagre(dto);
        } else {
            caseStoreConsumer.lagre(gsakSaksnummer.toString(), rinaSaksnummer);
        }
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
    @Transactional(readOnly = true)
    public Optional<FagsakRinasakKobling> finnVedRinaSaksnummer(String rinaSaksnummer) {
        return fagsakRinasakKoblingRepository.findByRinaSaksnummer(rinaSaksnummer);
    }

    /**
     * Søker etter saksnummer i både egen DB og eux-case-store
     * For også å fange opp evt. lovvalgs-sed'er som er opprettet fra nEESSI
     */
    @Transactional(readOnly = true)
    public Optional<Long> søkEtterSaksnummerFraRinaSaksnummer(String rinaSaksnummer) throws IntegrationException {
        Optional<Long> saksnummer = fagsakRinasakKoblingRepository.findByRinaSaksnummer(rinaSaksnummer)
                .map(FagsakRinasakKobling::getGsakSaksnummer);

        if (!saksnummer.isPresent()) {
            saksnummer = caseStoreConsumer.finnVedRinaSaksnummer(rinaSaksnummer).stream()
                    .findFirst().map(CaseStoreDto::getFagsaknummer).map(Long::parseLong);
        }

        return saksnummer;
    }
}
