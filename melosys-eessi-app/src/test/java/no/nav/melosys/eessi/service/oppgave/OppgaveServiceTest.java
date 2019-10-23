package no.nav.melosys.eessi.service.oppgave;

import no.nav.melosys.eessi.integration.oppgave.OppgaveConsumer;
import no.nav.melosys.eessi.integration.oppgave.OppgaveDto;
import no.nav.melosys.eessi.integration.oppgave.OpprettOppgaveResponseDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OppgaveServiceTest {

    @Mock
    private OppgaveConsumer oppgaveConsumer;

    private OppgaveService oppgaveService;

    @Captor
    private ArgumentCaptor<OppgaveDto> captor;

    @Before
    public void setup() {
        oppgaveService = new OppgaveService(oppgaveConsumer);
    }

    @Test
    public void opprettOppgaveIdOgFordeling_validerFelterBlirSatt() throws IntegrationException {

        when(oppgaveConsumer.opprettOppgave(any())).thenReturn(new OpprettOppgaveResponseDto());
        String journalpostID = "111";
        oppgaveService.opprettOppgaveTilIdOgFordeling(journalpostID, SedType.A009.name());

        verify(oppgaveConsumer).opprettOppgave(captor.capture());

        OppgaveDto oppgaveDto = captor.getValue();
        assertThat(oppgaveDto.getJournalpostId()).isEqualTo(journalpostID);
        assertThat(oppgaveDto.getTema()).isEqualTo("UFM");
        assertThat(oppgaveDto.getOppgavetype()).isEqualTo("JFR");
        assertThat(oppgaveDto.getBeskrivelse()).isEqualTo("EØS - A009");
    }
}