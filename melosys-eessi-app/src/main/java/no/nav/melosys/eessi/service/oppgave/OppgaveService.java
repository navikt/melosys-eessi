package no.nav.melosys.eessi.service.oppgave;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.oppgave.OppgaveConsumer;
import no.nav.melosys.eessi.integration.oppgave.OppgaveDto;
import no.nav.melosys.eessi.integration.oppgave.OpprettOppgaveResponseDto;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.stereotype.Service;
import static no.nav.melosys.eessi.service.sed.SedTypeTilTemaMapper.temaForSedType;

@Slf4j
@Service
public class OppgaveService {

    private static final String JFR = "JFR";
    private static final String ENHET_ID_FORDELING = "4303";
    private static final String BESKRIVELSE = "EØS - %s";

    private final OppgaveConsumer oppgaveConsumer;

    public OppgaveService(OppgaveConsumer oppgaveConsumer) {
        this.oppgaveConsumer = oppgaveConsumer;
    }

    public String opprettOppgaveTilIdOgFordeling(String journalpostID, String sedType) throws IntegrationException {
        OppgaveDto oppgaveDto = OppgaveDto.builder()
                .aktivDato(LocalDate.now())
                .fristFerdigstillelse(LocalDate.now().plusWeeks(2L))
                .journalpostId(journalpostID)
                .oppgavetype(JFR)
                .prioritet("NORM")
                .tema(temaForSedType(sedType))
                .tildeltEnhetsnr(ENHET_ID_FORDELING)
                .beskrivelse(String.format(BESKRIVELSE, sedType))
                .build();

        OpprettOppgaveResponseDto response = oppgaveConsumer.opprettOppgave(oppgaveDto);
        log.info("Oppgave til ID og fordeling opprettet med id {}", response.getId());
        return response.getId();
    }
}
