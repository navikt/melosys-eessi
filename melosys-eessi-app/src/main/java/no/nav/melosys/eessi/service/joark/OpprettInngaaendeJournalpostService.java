package no.nav.melosys.eessi.service.joark;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.gsak.GsakService;
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpprettInngaaendeJournalpostService {

    private final GsakService gsakService;
    private final JournalpostService journalpostService;
    private final JournalpostSedKoblingService journalpostSedKoblingService;

    @Autowired
    public OpprettInngaaendeJournalpostService(GsakService gsakService,
            JournalpostService journalpostService,
            JournalpostSedKoblingService journalpostSedKoblingService) {
        this.gsakService = gsakService;
        this.journalpostService = journalpostService;
        this.journalpostSedKoblingService = journalpostSedKoblingService;
    }

    public SakInformasjon arkiverInngaaendeSedHentSakinformasjon(SedHendelse sedMottatt, String aktoerId, byte[] sedPdf) throws IntegrationException {

        Sak sak = gsakService.hentEllerOpprettSak(sedMottatt.getRinaSakId(), aktoerId, BucType.valueOf(sedMottatt.getBucType()));
        log.info("Midlertidig journalfører rinaSak {}", sedMottatt.getRinaSakId());
        OpprettJournalpostResponse response = opprettJournalpostLagreRelasjon(sedMottatt, sak, sedPdf);
        log.info("Midlertidig journalpost opprettet med id {}", response.getJournalpostId());

        //fixme: midlertidig fix i påvente av at dokumentId skal bli returnert fra journalpostApi
        String dokumentId = response.getDokumenter() == null ? "ukjent" : response.getDokumenter().get(0).getDokumentInfoId();

        return SakInformasjon.builder().journalpostId(response.getJournalpostId())
                .dokumentId(dokumentId)
                .gsakSaksnummer(sak.getId())
                .build();
    }

    public String arkiverInngaaendeSedUtenBruker(SedHendelse sedHendelse, byte[] sedPdf) throws IntegrationException {
        return opprettJournalpostLagreRelasjon(sedHendelse, null, sedPdf).getJournalpostId();
    }

    private OpprettJournalpostResponse opprettJournalpostLagreRelasjon(SedHendelse sedMottatt, Sak sak, byte[] sedPdf) throws IntegrationException {
        OpprettJournalpostResponse response = journalpostService.opprettInngaaendeJournalpost(sedMottatt, sak, sedPdf);
        lagreJournalpostRelasjon(sedMottatt, response);
        return response;
    }

    private void lagreJournalpostRelasjon(SedHendelse sedHendelse, OpprettJournalpostResponse opprettJournalpostResponse) {
        journalpostSedKoblingService.lagre(
                opprettJournalpostResponse.getJournalpostId(), sedHendelse.getRinaSakId(), sedHendelse.getSedId(),
                sedHendelse.getRinaDokumentVersjon(), sedHendelse.getBucType(), sedHendelse.getSedType()
        );
    }
}
