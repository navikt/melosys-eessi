package no.nav.melosys.eessi.integration.oppgave;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

import lombok.SneakyThrows;
import no.nav.melosys.eessi.identifisering.oppgave.OppgaveConsumer;
import no.nav.melosys.eessi.identifisering.oppgave.OppgaveDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class OppgaveConsumerTest {

    private MockRestServiceServer server;
    private final RestTemplate restTemplate = new RestTemplate();

    private OppgaveConsumer oppgaveConsumer;

    private final String OPPGAVE_ID = "123";

    @BeforeEach
    public void setUp() {
        server = MockRestServiceServer.createServer(restTemplate);
        oppgaveConsumer = new OppgaveConsumer(restTemplate);
    }

    @Test
    void hentOppgave_oppgaveFinnes_verifiserMapping() {
        server.expect(requestTo("/oppgaver/" + OPPGAVE_ID))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess().contentType(MediaType.APPLICATION_JSON).body(hentOppgaveResponse()));
        var oppgave = oppgaveConsumer.hentOppgave(OPPGAVE_ID);

        assertThat(oppgave)
                .extracting(
                        OppgaveDto::getStatus,
                        OppgaveDto::getAktoerId,
                        OppgaveDto::getTema
                ).containsExactly(
                "AAPNET",
                "1332607802528",
                "MED"
        );
    }

    @Test
    void opprettOppgave_verifiserMapping() {
        server.expect(requestTo("/oppgaver"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess().contentType(MediaType.APPLICATION_JSON).body(hentOppgaveResponse()));
        var oppgaveDto = opprettOppgave();
        var oppgave = oppgaveConsumer.opprettOppgave(oppgaveDto);

        assertThat(oppgave)
                .extracting(
                        OppgaveDto::getJournalpostId,
                        OppgaveDto::getAktoerId,
                        OppgaveDto::getTema,
                        OppgaveDto::getTildeltEnhetsnr
                ).containsExactly(
                oppgave.getJournalpostId(),
                oppgave.getAktoerId(),
                oppgave.getTema(),
                oppgave.getTildeltEnhetsnr()
        );
    }

    @Test
    void oppdaterOppgave_verifiserMapping() {
        server.expect(requestTo("/oppgaver/" + OPPGAVE_ID))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess().contentType(MediaType.APPLICATION_JSON).body(hentOppgaveResponse()));
        var oppgaveDto = opprettOppgave();
        var oppgave = oppgaveConsumer.oppdaterOppgave(OPPGAVE_ID, oppgaveDto);

        assertThat(oppgave)
                .extracting(
                        OppgaveDto::getJournalpostId,
                        OppgaveDto::getAktoerId,
                        OppgaveDto::getTema,
                        OppgaveDto::getTildeltEnhetsnr
                ).containsExactly(
                oppgave.getJournalpostId(),
                oppgave.getAktoerId(),
                oppgave.getTema(),
                oppgave.getTildeltEnhetsnr()
        );
    }

    private OppgaveDto opprettOppgave() {
        return OppgaveDto.builder()
                .aktivDato(LocalDate.now())
                .fristFerdigstillelse(LocalDate.now().plusDays(1))
                .journalpostId("11111")
                .aktoerId("1332607802528")
                .tema("MED")
                .tildeltEnhetsnr("4530")
                .build();
    }

    @SneakyThrows
    private String hentOppgaveResponse() {
        return new String(
                Files.readAllBytes(
                        Paths.get(
                                Optional.ofNullable(getClass().getClassLoader().getResource("mock/oppgave_get.json"))
                                        .orElseThrow()
                                        .toURI()
                        )
                )
        );
    }
}
