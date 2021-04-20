package no.nav.melosys.eessi.integration.oppgave;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
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
    public void hentOppgave_oppgaveFinnes_verifiserMapping() {
        server.expect(requestTo("/oppgaver/" + OPPGAVE_ID))
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
    public void opprettOppgave_verifiserMapping() {
        server.expect(requestTo("/oppgaver"))
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