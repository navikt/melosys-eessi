package no.nav.melosys.eessi.service.oppgave;

import no.nav.melosys.eessi.integration.oppgave.HentOppgaveDto;
import no.nav.melosys.eessi.integration.oppgave.OppgaveConsumer;
import no.nav.melosys.eessi.integration.oppgave.OppgaveDto;
import no.nav.melosys.eessi.integration.oppgave.OppgaveOppdateringDto;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.SedType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OppgaveServiceTest {

    @Mock
    private OppgaveConsumer oppgaveConsumer;

    private OppgaveService oppgaveService;

    @Captor
    private ArgumentCaptor<OppgaveDto> captor;

    @BeforeEach
    public void setup() {
        oppgaveService = new OppgaveService(oppgaveConsumer);
    }

    @Test
    void opprettOppgaveIdOgFordeling_validerFelterBlirSatt() {

        when(oppgaveConsumer.opprettOppgave(any())).thenReturn(new HentOppgaveDto());
        String journalpostID = "111";
        String rinaSaksnummer = "123";
        oppgaveService.opprettOppgaveTilIdOgFordeling(journalpostID, SedType.A009.name(), rinaSaksnummer);

        verify(oppgaveConsumer).opprettOppgave(captor.capture());

        OppgaveDto oppgaveDto = captor.getValue();
        assertThat(oppgaveDto.getJournalpostId()).isEqualTo(journalpostID);
        assertThat(oppgaveDto.getTema()).isEqualTo("UFM");
        assertThat(oppgaveDto.getOppgavetype()).isEqualTo("JFR");
        assertThat(oppgaveDto.getBeskrivelse()).isEqualTo("EØS - A009\n\rRina-sak - " + rinaSaksnummer);
    }

    @Test
    void opprettUtgåendeJfrOppgave_validerFelter() {
        when(oppgaveConsumer.opprettOppgave(any())).thenReturn(new HentOppgaveDto());
        String journalpostID = "111";
        String aktørID = "321";
        String rinaUrl = "https://test.local";
        SedHendelse sedHendelse = SedHendelse.builder()
                .sedType("A009")
                .rinaSakId("12345")
                .rinaDokumentId("deadbeef")
                .build();

        oppgaveService.opprettUtgåendeJfrOppgave(journalpostID, sedHendelse, aktørID, rinaUrl);

        verify(oppgaveConsumer).opprettOppgave(captor.capture());

        OppgaveDto oppgaveDto = captor.getValue();
        assertThat(oppgaveDto.getJournalpostId()).isEqualTo(journalpostID);
        assertThat(oppgaveDto.getTema()).isEqualTo("MED");
        assertThat(oppgaveDto.getOppgavetype()).isEqualTo("JFR_UT");
        assertThat(oppgaveDto.getAktoerId()).isEqualTo(aktørID);
        assertThat(oppgaveDto.getBeskrivelse()).contains("A009", "deadbeef");
    }

    @Test
    void ferdigstillOppgave_validerStatusFerdigstilt() {
        final var oppgaveID = "22222";
        final var oppgaveVersjon = 4;
        var captor = ArgumentCaptor.forClass(OppgaveOppdateringDto.class);

        oppgaveService.ferdigstillOppgave(oppgaveID, oppgaveVersjon);

        verify(oppgaveConsumer).oppdaterOppgave(eq(oppgaveID), captor.capture());
        assertThat(captor.getValue())
                .extracting(OppgaveOppdateringDto::getId, OppgaveOppdateringDto::getVersjon, OppgaveOppdateringDto::getStatus, OppgaveOppdateringDto::getBeskrivelse)
                .containsExactly(Integer.parseInt(oppgaveID), oppgaveVersjon, "FERDIGSTILT", null);
    }
}
