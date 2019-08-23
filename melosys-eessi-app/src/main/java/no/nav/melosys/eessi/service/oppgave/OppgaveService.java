package no.nav.melosys.eessi.service.oppgave;

import java.time.LocalDate;
import no.nav.melosys.eessi.integration.gsak.oppgave.OppgaveConsumer;
import no.nav.melosys.eessi.integration.gsak.oppgave.OppgaveDto;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.stereotype.Service;

@Service
public class OppgaveService {

    private final OppgaveConsumer oppgaveConsumer;

    public OppgaveService(OppgaveConsumer oppgaveConsumer) {
        this.oppgaveConsumer = oppgaveConsumer;
    }

    public OppgaveDto opprettOppgaveTilIdOgFordeling(String journalpostID) throws IntegrationException {
        //TODO: parametere til Oppgave må avklares
        OppgaveDto oppgaveDto = OppgaveDto.builder()
                .aktivDato(LocalDate.now())
                .aktoerId("")
                .behandlesAvApplikasjon("manuell - 9999 ?")
                .behandlingstema("???")
                .behandlingstype("???")
                .fristFerdigstillelse(LocalDate.now().plusDays(1L))
                .journalpostId(journalpostID)
                .oppgavetype("BEH_SAK_MK ? rekvirer/identifiser?")
                .prioritet("HOY")
                .saksreferanse("finnes ikke")
                .tema("MED..?")
                .tildeltEnhetsnr("NAV id fordeling enhetsnr")
                .tilordnetRessurs(null)
                .build();
        oppgaveConsumer.opprettOppgave(oppgaveDto);

        return null;
    }
}
