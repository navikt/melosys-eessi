package no.nav.melosys.eessi.service.joark;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpprettInngaaendeJournalpostService {

    private final SaksrelasjonService saksrelasjonService;
    private final JournalpostService journalpostService;
    private final JournalpostSedKoblingService journalpostSedKoblingService;

    @Autowired
    public OpprettInngaaendeJournalpostService(SaksrelasjonService saksrelasjonService,
                                               JournalpostService journalpostService,
                                               JournalpostSedKoblingService journalpostSedKoblingService) {
        this.saksrelasjonService = saksrelasjonService;
        this.journalpostService = journalpostService;
        this.journalpostSedKoblingService = journalpostSedKoblingService;
    }

    public SakInformasjon arkiverInngaaendeSedHentSakinformasjon(
            SedHendelse sedMottatt, SedMedVedlegg sedMedVedlegg, String personIdent) {

        Optional<Sak> arkivsak = saksrelasjonService.finnArkivsakForRinaSaksnummer(sedMottatt.getRinaSakId());

        log.info("Midlertidig journalfører rinaSak {}", sedMottatt.getRinaSakId());
        OpprettJournalpostResponse response = opprettJournalpostLagreRelasjon(
            sedMottatt, arkivsak.orElse(null), sedMedVedlegg, personIdent);
        log.info("Midlertidig journalpost opprettet med id {}", response.getJournalpostId());

        String dokumentId = response.getDokumenter() == null
                ? "ukjent" : response.getDokumenter().get(0).getDokumentInfoId();

        return SakInformasjon.builder().journalpostId(response.getJournalpostId())
                .dokumentId(dokumentId)
                .gsakSaksnummer(arkivsak.map(Sak::getId).orElse(null))
                .build();
    }

    public String arkiverInngaaendeSedUtenBruker(SedHendelse sedHendelse, SedMedVedlegg sedMedVedlegg, String personIdent) {
        return opprettJournalpostLagreRelasjon(sedHendelse, null, sedMedVedlegg, personIdent).getJournalpostId();
    }

    private OpprettJournalpostResponse opprettJournalpostLagreRelasjon(
            SedHendelse sedMottatt, Sak sak, SedMedVedlegg sedMedVedlegg, String personIdent) {
        OpprettJournalpostResponse response = journalpostService.opprettInngaaendeJournalpost(sedMottatt, sak, sedMedVedlegg, personIdent);
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
