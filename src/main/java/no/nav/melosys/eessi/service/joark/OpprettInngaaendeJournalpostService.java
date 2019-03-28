package no.nav.melosys.eessi.service.joark;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.MottaInngaaendeForsendelseRequest;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.MottaInngaaendeForsendelseResponse;
import no.nav.melosys.eessi.integration.dokmotinngaaende.DokmotInngaaendeConsumer;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.CaseRelation;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.repository.CaseRelationRepository;
import no.nav.melosys.eessi.service.dokkat.DokkatSedInfo;
import no.nav.melosys.eessi.service.dokkat.DokkatService;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.gsak.GsakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static no.nav.melosys.eessi.service.joark.InngaaendeForsendelseMapper.createMottaInngaaendeForsendelseRequest;

@Slf4j
@Service
public class OpprettInngaaendeJournalpostService {

    private final DokmotInngaaendeConsumer dokmotInngaaendeConsumer;
    private final CaseRelationRepository caseRelationRepository;
    private final DokkatService dokkatService;
    private final GsakService gsakService;
    private final EuxService euxService;

    @Autowired
    public OpprettInngaaendeJournalpostService(DokmotInngaaendeConsumer dokmotInngaaendeConsumer,
                                               CaseRelationRepository caseRelationRepository,
                                               DokkatService dokkatService,
                                               GsakService gsakService,
                                               EuxService euxService) {
        this.dokmotInngaaendeConsumer = dokmotInngaaendeConsumer;
        this.caseRelationRepository = caseRelationRepository;
        this.dokkatService = dokkatService;
        this.gsakService = gsakService;
        this.euxService = euxService;
    }

    public String arkiverInngaaendeSed(SedHendelse sedMottatt, String aktoerId) throws IntegrationException {

        Sak sak = getOrCreateSak(sedMottatt.getRinaSakId(), aktoerId);
        DokkatSedInfo dokkatSedInfo = dokkatService.hentMetadataFraDokkat(sedMottatt.getSedType());
        ParticipantInfo sender = euxService.hentUtsender(sedMottatt.getRinaSakId());

        byte[] pdf = euxService.hentSedPdf(sedMottatt.getRinaSakId(), sedMottatt.getRinaDokumentId());
        MottaInngaaendeForsendelseRequest request = createMottaInngaaendeForsendelseRequest(
                aktoerId, sedMottatt, sak, dokkatSedInfo, sender, pdf);

        log.info("Midlertidig journalfører rinaSak {}", sedMottatt.getRinaSakId());
        MottaInngaaendeForsendelseResponse response = dokmotInngaaendeConsumer.create(request);
        return response.getJournalpostId();
    }

    private Sak getOrCreateSak(String rinaId, String aktoerId) throws IntegrationException {
        Optional<Long> gsakId = caseRelationRepository.findByRinaId(rinaId)
                .map(CaseRelation::getGsakSaksnummer);

        if (gsakId.isPresent()) {
            log.info("Henter gsak med id: {}", gsakId.get());
            return gsakService.getSak(gsakId.get());
        } else {
            log.info("Oppretter ny sak i gsak for rinaSak {}", rinaId);
            return createSak(rinaId, aktoerId);
        }
    }

    private Sak createSak(String rinaId, String aktoerId) throws IntegrationException {
        Sak sak = gsakService.createSak(aktoerId);

        if (sak == null) {
            throw new IntegrationException("Sak ble ikke opprettet");
        }

        CaseRelation caseRelation = new CaseRelation();
        caseRelation.setRinaId(rinaId);
        caseRelation.setGsakSaksnummer(Long.parseLong(sak.getId()));
        caseRelationRepository.save(caseRelation);

        log.info("Sak i gsak med id {} ble opprettet for rinaSak {}", sak.getId(), rinaId);
        return sak;
    }
}
