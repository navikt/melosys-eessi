package no.nav.melosys.eessi.service.oppgave;

import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.oppgave.HentOppgaveDto;
import no.nav.melosys.eessi.identifisering.oppgave.OppgaveConsumer;
import no.nav.melosys.eessi.identifisering.oppgave.OppgaveDto;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.springframework.stereotype.Service;

import static no.nav.melosys.eessi.service.sed.SedTypeTilTemaMapper.temaForSedType;

@Slf4j
@Service
public class OppgaveService {

    private static final String JFR = "JFR";
    private static final String JFR_UT = "JFR_UT";
    private static final String ENHET_ID_FORDELING = "4303";
    private static final String ENHET_MEDLEMSKAP_OG_AVGIFT = "4530";
    private static final String PRIORITET_NORMAL = "NORM";
    private static final String BESKRIVELSE = "EØS - %s\n\rRina-sak - %s";

    private final OppgaveConsumer oppgaveConsumer;

    public OppgaveService(OppgaveConsumer oppgaveConsumer) {
        this.oppgaveConsumer = oppgaveConsumer;
    }

    public OppgaveDto hentOppgave(String oppgaveID) {
        return oppgaveConsumer.hentOppgave(oppgaveID);
    }

    public String opprettOppgaveTilIdOgFordeling(String journalpostID, String sedType, String rinaSaksnummer) {
        var oppgaveDto = OppgaveDto.builder()
                .aktivDato(LocalDate.now())
                .fristFerdigstillelse(LocalDate.now().plusDays(1))
                .journalpostId(journalpostID)
                .oppgavetype(JFR)
                .prioritet(PRIORITET_NORMAL)
                .tema(temaForSedType(sedType))
                .tildeltEnhetsnr(ENHET_ID_FORDELING)
                .beskrivelse(String.format(BESKRIVELSE, sedType, rinaSaksnummer))
                .build();

        HentOppgaveDto response = oppgaveConsumer.opprettOppgave(oppgaveDto);
        log.info("Oppgave til ID og fordeling opprettet med id {}", response.getId());
        return response.getId();
    }

    public String opprettUtgåendeJfrOppgave(String journalpostID, SedHendelse sedHendelse, String aktørId, String rinaUrl) {
        var oppgaveDto = OppgaveDto.builder()
                .aktivDato(LocalDate.now())
                .beskrivelse(lagBeskrivelseUtgåendeJfrOppgave(sedHendelse, rinaUrl))
                .fristFerdigstillelse(LocalDate.now().plusDays(1))
                .journalpostId(journalpostID)
                .oppgavetype(JFR_UT)
                .prioritet(PRIORITET_NORMAL)
                .tema("MED") //Utgående vil alltid være MED
                .tildeltEnhetsnr(ENHET_MEDLEMSKAP_OG_AVGIFT)
                .aktoerId(aktørId)
                .build();

        HentOppgaveDto response = oppgaveConsumer.opprettOppgave(oppgaveDto);
        log.info("Utgående journalføringsoppgave opprettet med id {}", response.getId());
        return response.getId();
    }

    private static String lagBeskrivelseUtgåendeJfrOppgave(SedHendelse sedHendelse, String rinaUrl) {
        return String.format("%s, rinaSakId=%s, rinaDokumentId=%s, link=%s",
                sedHendelse.getSedType(), sedHendelse.getRinaSakId(), sedHendelse.getRinaDokumentId(), rinaUrl);
    }

    public void ferdigstillOppgave(String oppgaveId) {
        log.info("Ferdigstiller oppgave {}", oppgaveId);
        var oppgave = oppgaveConsumer.hentOppgave(oppgaveId);
        oppgave.setStatus("FERDIGSTILT");
        oppgaveConsumer.oppdaterOppgave(oppgaveId, oppgave);
    }
}
