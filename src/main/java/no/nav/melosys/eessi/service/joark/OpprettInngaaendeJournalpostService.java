package no.nav.melosys.eessi.service.joark;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.gsak.GsakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpprettInngaaendeJournalpostService {

    private final GsakService gsakService;
    private final JournalpostService journalpostService;

    @Autowired
    public OpprettInngaaendeJournalpostService(GsakService gsakService,
            JournalpostService journalpostService) {
        this.gsakService = gsakService;
        this.journalpostService = journalpostService;
    }

    public SakInformasjon arkiverInngaaendeSedHentSakinformasjon(SedHendelse sedMottatt, String aktoerId, byte[] sedPdf) throws IntegrationException {

        Sak sak = gsakService.hentEllerOpprettSak(sedMottatt.getRinaSakId(), aktoerId, BucType.valueOf(sedMottatt.getBucType()));
        log.info("Midlertidig journalfører rinaSak {}", sedMottatt.getRinaSakId());
        OpprettJournalpostResponse response = journalpostService.opprettInngaaendeJournalpost(sedMottatt, sak, sedPdf);

        return SakInformasjon.builder().journalpostId(response.getJournalpostId())
                .dokumentId(response.getDokumenter().get(0))
                .gsakSaksnummer(sak.getId())
                .build();
    }
}
