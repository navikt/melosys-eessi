package no.nav.melosys.eessi.service.oppgave;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.gsak.oppgave.OppgaveConsumer;
import no.nav.melosys.eessi.integration.gsak.oppgave.OppgaveDto;
import no.nav.melosys.eessi.integration.gsak.oppgave.OpprettOppgaveResponseDto;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OppgaveService {

    private static final String SOEK_PERSON = "SOEK_PERSON";
    private static final String TEMA_MED = "MED";
    private static final String ENHET_ID_FORDELING = "4303";

    private final OppgaveConsumer oppgaveConsumer;

    public OppgaveService(OppgaveConsumer oppgaveConsumer) {
        this.oppgaveConsumer = oppgaveConsumer;
    }

    public String opprettOppgaveTilIdOgFordeling(String journalpostID) throws IntegrationException {
        OppgaveDto oppgaveDto = OppgaveDto.builder()
                .aktivDato(LocalDate.now())
                .fristFerdigstillelse(LocalDate.now().plusWeeks(2L))
                .journalpostId(journalpostID)
                .oppgavetype(SOEK_PERSON)
                .prioritet("HOY")
                .tema(TEMA_MED)
                .tildeltEnhetsnr(ENHET_ID_FORDELING)
                .build();
        OpprettOppgaveResponseDto response = oppgaveConsumer.opprettOppgave(oppgaveDto);
        log.info("Oppgave til ID og fordeling med id {}", response.getId());
        return response.getId();
    }
}
