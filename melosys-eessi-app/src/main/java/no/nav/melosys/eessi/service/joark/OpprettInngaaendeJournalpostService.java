package no.nav.melosys.eessi.service.joark;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService;
import no.nav.melosys.eessi.service.sak.GsakService;
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

    public SakInformasjon arkiverInngaaendeSedHentSakinformasjon(
            SedHendelse sedMottatt, SedMedVedlegg sedMedVedlegg) throws IntegrationException {

        Sak sak = gsakService.finnSakForRinaID(sedMottatt.getRinaSakId()).orElse(null);
        log.info("Midlertidig journalfører rinaSak {}", sedMottatt.getRinaSakId());
        OpprettJournalpostResponse response = opprettJournalpostLagreRelasjon(sedMottatt, sak, sedMedVedlegg);
        log.info("Midlertidig journalpost opprettet med id {}", response.getJournalpostId());

        String dokumentId = response.getDokumenter() == null
                ? "ukjent" : response.getDokumenter().get(0).getDokumentInfoId();

        return SakInformasjon.builder().journalpostId(response.getJournalpostId())
                .dokumentId(dokumentId)
                .gsakSaksnummer(sak != null ? sak.getId() : null)
                .build();
    }

    public String arkiverInngaaendeSedUtenBruker(SedHendelse sedHendelse, SedMedVedlegg sedMedVedlegg) throws IntegrationException {
        return opprettJournalpostLagreRelasjon(sedHendelse, null, sedMedVedlegg).getJournalpostId();
    }

    private OpprettJournalpostResponse opprettJournalpostLagreRelasjon(
            SedHendelse sedMottatt, Sak sak, SedMedVedlegg sedMedVedlegg) throws IntegrationException {
        OpprettJournalpostResponse response = journalpostService.opprettInngaaendeJournalpost(sedMottatt, sak, sedMedVedlegg);
        lagreJournalpostRelasjon(sedMottatt, response);
        return response;
    }

    private void lagreJournalpostRelasjon(
            SedHendelse sedHendelse, OpprettJournalpostResponse opprettJournalpostResponse) {
        journalpostSedKoblingService.lagre(
                opprettJournalpostResponse.getJournalpostId(), sedHendelse.getRinaSakId(),
                sedHendelse.getRinaDokumentId(), sedHendelse.getRinaDokumentVersjon(),
                sedHendelse.getBucType(), sedHendelse.getSedType()
        );
    }
}
