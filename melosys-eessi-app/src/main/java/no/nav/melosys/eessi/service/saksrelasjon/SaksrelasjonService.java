package no.nav.melosys.eessi.service.saksrelasjon;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreConsumer;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreDto;
import no.nav.melosys.eessi.integration.sak.SakConsumer;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.repository.FagsakRinasakKoblingRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class SaksrelasjonService {

    private final FagsakRinasakKoblingRepository fagsakRinasakKoblingRepository;
    private final CaseStoreConsumer caseStoreConsumer;
    private final SakConsumer sakConsumer;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public FagsakRinasakKobling lagreKobling(Long gsakSaksnummer, String rinaSaksnummer, BucType bucType) {
        FagsakRinasakKobling fagsakRinasakKobling = new FagsakRinasakKobling();
        fagsakRinasakKobling.setRinaSaksnummer(rinaSaksnummer);
        fagsakRinasakKobling.setGsakSaksnummer(gsakSaksnummer);
        fagsakRinasakKobling.setBucType(bucType);
        fagsakRinasakKobling = fagsakRinasakKoblingRepository.save(fagsakRinasakKobling);

        if (!bucType.erLovvalgBuc()) {
            oppdaterEllerLagreIEuxCaseStore(gsakSaksnummer, rinaSaksnummer);
        }

        applicationEventPublisher.publishEvent(new SaksrelasjonOpprettetEvent(rinaSaksnummer, gsakSaksnummer));
        return fagsakRinasakKobling;
    }

    private void oppdaterEllerLagreIEuxCaseStore(Long gsakSaksnummer, String rinaSaksnummer) {
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
    public Optional<Long> søkEtterSaksnummerFraRinaSaksnummer(String rinaSaksnummer) {
        Optional<Long> saksnummer = fagsakRinasakKoblingRepository.findByRinaSaksnummer(rinaSaksnummer)
                .map(FagsakRinasakKobling::getGsakSaksnummer);

        if (!saksnummer.isPresent()) {
            saksnummer = caseStoreConsumer.finnVedRinaSaksnummer(rinaSaksnummer).stream()
                    .findFirst().map(CaseStoreDto::getFagsaknummer).map(Long::parseLong);
        }

        return saksnummer;
    }
}
