package no.nav.melosys.eessi.service.joark;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.MottaInngaaendeForsendelseRequest;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.MottaInngaaendeForsendelseResponse;
import no.nav.melosys.eessi.integration.dokmotinngaaende.DokmotInngaaendeConsumer;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.caserelation.SaksrelasjonService;
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
    private final SaksrelasjonService saksrelasjonService;
    private final DokkatService dokkatService;
    private final GsakService gsakService;
    private final EuxService euxService;

    @Autowired
    public OpprettInngaaendeJournalpostService(
            DokmotInngaaendeConsumer dokmotInngaaendeConsumer,
            SaksrelasjonService saksrelasjonService,
            DokkatService dokkatService,
            GsakService gsakService,
            EuxService euxService) {
        this.dokmotInngaaendeConsumer = dokmotInngaaendeConsumer;
        this.saksrelasjonService = saksrelasjonService;
        this.dokkatService = dokkatService;
        this.gsakService = gsakService;
        this.euxService = euxService;
    }

    public SakInformasjon arkiverInngaaendeSedHentSakinformasjon(SedHendelse sedMottatt, String aktoerId) throws IntegrationException {

        Sak sak = hentEllerOpprettSak(sedMottatt.getRinaSakId(), aktoerId, BucType.valueOf(sedMottatt.getBucType()));
        DokkatSedInfo dokkatSedInfo = dokkatService.hentMetadataFraDokkat(sedMottatt.getSedType());
        ParticipantInfo sender = euxService.hentUtsender(sedMottatt.getRinaSakId());

        byte[] pdf = euxService.hentSedPdf(sedMottatt.getRinaSakId(), sedMottatt.getRinaDokumentId());
        MottaInngaaendeForsendelseRequest request = createMottaInngaaendeForsendelseRequest(
                aktoerId, sedMottatt, sak, dokkatSedInfo, sender, pdf);

        log.info("Midlertidig journalfører rinaSak {}", sedMottatt.getRinaSakId());
        MottaInngaaendeForsendelseResponse response = dokmotInngaaendeConsumer.create(request);

        return SakInformasjon.builder().journalpostId(response.getJournalpostId())
                .dokumentId(response.getDokumentIdListe().get(0))
                .gsakSaksnummer(sak.getId())
                .build();
    }

    private Sak hentEllerOpprettSak(String rinaId, String aktoerId, BucType bucType) throws IntegrationException {
        Optional<Long> gsakId = saksrelasjonService.finnVedRinaId(rinaId)
                .map(FagsakRinasakKobling::getGsakSaksnummer);

        if (gsakId.isPresent()) {
            log.info("Henter gsak med id: {}", gsakId.get());
            return gsakService.getSak(gsakId.get());
        } else {
            log.info("Oppretter ny sak i gsak for rinaSak {}", rinaId);
            return opprettSak(rinaId, aktoerId, bucType);
        }
    }

    private Sak opprettSak(String rinaId, String aktoerId, BucType bucType) throws IntegrationException {
        Sak sak = gsakService.opprettSak(aktoerId);

        if (sak == null) {
            throw new IntegrationException("Sak ble ikke opprettet");
        }

        saksrelasjonService.lagreKobling(Long.parseLong(sak.getId()), rinaId, bucType);
        log.info("Sak i gsak med id {} ble opprettet for rinaSak {}", sak.getId(), rinaId);
        return sak;
    }
}
