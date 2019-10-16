package no.nav.melosys.eessi.service.joark;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.sak.SakService;
import no.nav.melosys.eessi.service.tps.TpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpprettUtgaaendeJournalpostService {

    private final SakService sakService;
    private final JournalpostService journalpostService;
    private final EuxService euxService;
    private final TpsService tpsService;

    @Autowired
    public OpprettUtgaaendeJournalpostService(
            SakService sakService, JournalpostService journalpostService, EuxService euxService,
            TpsService tpsService) {
        this.journalpostService = journalpostService;
        this.sakService = sakService;
        this.euxService = euxService;
        this.tpsService = tpsService;
    }

    public String arkiverUtgaaendeSed(SedHendelse sedSendt) throws IntegrationException, NotFoundException {
        Sak sak = sakService.finnSakForRinaSaksnummer(sedSendt.getRinaSakId())
                .orElseThrow(() -> new NotFoundException("Finner ikke nav-sak for rinasaksnummer " + sedSendt.getRinaSakId()));

        log.info("Journalfører dokument: {}", sedSendt.getRinaDokumentId());
        String navIdent = tpsService.hentNorskIdent(sak.getAktoerId());
        OpprettJournalpostResponse response = journalpostService.opprettUtgaaendeJournalpost(sedSendt, sak,
                euxService.hentSedMedVedlegg(sedSendt.getRinaSakId(), sedSendt.getRinaDokumentId()), navIdent);

        return response.getJournalpostId();
    }
}
