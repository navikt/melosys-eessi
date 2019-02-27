package no.nav.melosys.eessi.service.joark;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokarkivsed.api.v1.ArkiverUtgaaendeSed;
import no.nav.eessi.basis.SedSendt;
import no.nav.melosys.eessi.integration.dokarkivsed.DokarkivSedConsumer;
import no.nav.melosys.eessi.integration.dokarkivsed.OpprettUtgaaendeJournalpostResponse;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.models.CaseRelation;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.repository.CaseRelationRepository;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.gsak.GsakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static no.nav.melosys.eessi.service.joark.ForsendelseInformasjonMapper.createForsendelse;
import static no.nav.melosys.eessi.service.joark.ForsendelseInformasjonMapper.hoveddokument;

@Slf4j
@Service
public class OpprettUtgaaendeJournalpostService {

    private final GsakService gsakService;
    private final DokarkivSedConsumer dokarkivSedConsumer;
    private final EuxService euxService;
    private final CaseRelationRepository caseRelationRepository;

    @Autowired
    public OpprettUtgaaendeJournalpostService(
            GsakService gsakService,
            DokarkivSedConsumer dokarkivSedConsumer, EuxService euxService,
            CaseRelationRepository caseRelationRepository) {
        this.dokarkivSedConsumer = dokarkivSedConsumer;
        this.euxService = euxService;
        this.gsakService = gsakService;
        this.caseRelationRepository = caseRelationRepository;
    }

    //Returnerer journalpostId. Trengs returverdi?
    public String arkiverUtgaaendeSed(SedSendt sedSendt) throws IntegrationException, NotFoundException {

        byte[] pdf = SedDocumentStub.getPdfStub();

        Long gsakSaksnummer = caseRelationRepository.findByRinaId(sedSendt.getRinaSakId())
                .map(CaseRelation::getGsakSaksnummer).orElseThrow(() -> new NotFoundException("Saksrelasjon ikke funnet med rinaSakId " + sedSendt.getRinaSakId()));

        log.info("Henter gsak med id: {}", gsakSaksnummer);
        Sak sak = gsakService.getSak(gsakSaksnummer);
        ParticipantInfo receiver = euxService.hentMottaker(sedSendt.getRinaSakId());

        log.info("Journalfører dokument: {}", sedSendt.getRinaDokumentId());
        ArkiverUtgaaendeSed arkiverUtgaaendeSed = ArkiverUtgaaendeSed.builder()
                .forsendelsesinformasjon(createForsendelse(sak.getAktoerId(), sedSendt, sak, receiver))
//                .dokumentInfoVedleggListe(dokumentInfoVedleggListe(sedSendt))
                .dokumentInfoHoveddokument(hoveddokument(sedSendt.getSedType(), pdf))
                .build();

        OpprettUtgaaendeJournalpostResponse repsonse = dokarkivSedConsumer.create(arkiverUtgaaendeSed);

        return repsonse.getJournalpostId();
    }

//  TODO: venter på avklaring rundt potensielle endringer ved vedlegg til journalpost
//  private List<DokumentInfoVedlegg> dokumentInfoVedleggListe(SedSendt sedSendt) {}
}
