package no.nav.melosys.eessi.service.joark;

import java.util.List;
import java.util.Optional;

import io.getunleash.Unleash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.PersonIdentifisering;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.integration.journalpostapi.SedAlleredeJournalførtException;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.metrikker.SedMetrikker;
import no.nav.melosys.eessi.models.SedSendtHendelse;
import no.nav.melosys.eessi.repository.SedSendtHendelseRepository;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpprettUtgaaendeJournalpostService {

    private final SaksrelasjonService saksrelasjonService;
    private final JournalpostService journalpostService;
    private final EuxService euxService;
    private final PersonFasade personFasade;
    private final OppgaveService oppgaveService;
    private final SedMetrikker sedMetrikker;
    private final PersonIdentifisering personIdentifisering;
    private final SedSendtHendelseRepository sedSendtHendelseRepository;
    private final Unleash unleash;


    public void behandleSedSendtHendelse(SedHendelse sedSendt) {
        try {
                if (sedInneholderPersonId(sedSendt)) {
                    String journalpostId = arkiverUtgaaendeSed(sedSendt);
                    log.info("Journalpost opprettet med id: {}", journalpostId);
                    journalfoerTidligereSedDersomEksisterer(sedSendt.getRinaSakId());
                } else {
                    log.info("SED {} inneholder ikke personId, journalfører ikke.", sedSendt.getRinaDokumentId());
                    sedSendtHendelseRepository.save(new SedSendtHendelse(sedSendt.getId(), sedSendt, null));
                }

            sedMetrikker.sedSendt(sedSendt.getSedType());

        } catch (SedAlleredeJournalførtException e) {
            log.info("SED {} allerede journalført", e.getSedID());
        }
    }

    public void journalfoerTidligereSedDersomEksisterer(String rinaSakId) {
            List<SedSendtHendelse> sedSendtHendelser = sedSendtHendelseRepository.findAllByRinaSaksnummerAndAndJournalpostIdIsNull(rinaSakId);
            for (SedSendtHendelse sedSendtHendelse : sedSendtHendelser) {
                arkiverUtgaaendeSed(sedSendtHendelse.getSedHendelse());
                sedSendtHendelseRepository.delete(sedSendtHendelse);
            }
        log.info("Journalført {} tidligere utgående SEDer", sedSendtHendelser.size());
    }

    private boolean sedInneholderPersonId(SedHendelse sedHendelse) {
        final var rinaSaksId = sedHendelse.getRinaSakId();

         var sed = euxService.hentSedMedRetry(rinaSaksId,
            sedHendelse.getRinaDokumentId());

        log.info("Søker etter person for SED");
        Optional<String> person = personIdentifisering.identifiserPerson(rinaSaksId, sed);
        return person.isPresent();
    }

    public String arkiverUtgaaendeSed(SedHendelse sedSendt) {
        Optional<Sak> sak = saksrelasjonService.finnArkivsakForRinaSaksnummer(sedSendt.getRinaSakId());
        if (sak.isEmpty()) {
            return arkiverUtenSak(sedSendt);
        }

        return arkiverMedSak(sedSendt, sak.get());
    }

    private String arkiverMedSak(SedHendelse sedSendt, Sak sak) {
        log.info("Journalfører dokument: {}", sedSendt.getRinaDokumentId());
        String navIdent = personFasade.hentNorskIdent(sak.getAktoerId());
        OpprettJournalpostResponse response = opprettUtgåendeJournalpost(sedSendt, sak, navIdent);

        if (!"ENDELIG".equalsIgnoreCase(response.getJournalstatus())) {
            log.info("Journalpost {} ble ikke endelig journalført. Melding: {}", response.getJournalpostId(), response.getMelding());
            opprettUtgåendeJournalføringsoppgave(sedSendt, response.getJournalpostId(), personFasade.hentAktoerId(navIdent));
        }

        return response.getJournalpostId();
    }

    private String arkiverUtenSak(SedHendelse sedSendt) {
        log.info("Journalfører dokument uten sakstilknytning: {}", sedSendt.getRinaDokumentId());

        String navIdent = sedSendt.getNavBruker();
        OpprettJournalpostResponse response = opprettUtgåendeJournalpost(sedSendt, null, navIdent);
        opprettUtgåendeJournalføringsoppgave(sedSendt, response.getJournalpostId(), navIdent);

        return response.getJournalpostId();
    }

    private OpprettJournalpostResponse opprettUtgåendeJournalpost(SedHendelse sedSendt, Sak sak, String navIdent) {
        return journalpostService.opprettUtgaaendeJournalpost(sedSendt, sak,
                euxService.hentSedMedVedlegg(sedSendt.getRinaSakId(), sedSendt.getRinaDokumentId()), navIdent);
    }

    private String opprettUtgåendeJournalføringsoppgave(SedHendelse sedSendt, String journalpostId, String navIdent) {
        return oppgaveService.opprettUtgåendeJfrOppgave(journalpostId, sedSendt, isNotEmpty(navIdent) ? personFasade.hentAktoerId(navIdent) : null,
                euxService.hentRinaUrl(sedSendt.getRinaSakId()));
    }
}
