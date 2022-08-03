package no.nav.melosys.eessi;

import java.util.List;
import java.util.Random;

import no.nav.melosys.eessi.controller.dto.KafkaConsumerResponse;
import no.nav.melosys.eessi.integration.oppgave.HentOppgaveDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class KafkaAdminTjenesteTestIT extends ComponentTestBase {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void hentKafkaConsumers_returnerInformasjonOmAlleConsumere() {
        List<KafkaConsumerResponse> kafkaConsumerResponses = testRestTemplate.exchange(
            "http://localhost:" + port + "/api/admin/kafka/consumers",
            HttpMethod.GET,
            lagStringHttpEntity(),
            new ParameterizedTypeReference<List<KafkaConsumerResponse>>() {
            }).getBody();

        assertThat(kafkaConsumerResponses)
            .hasSize(4);
    }

    @Test
    void stoppOgStartConsumer_consumerStoppedOgStartes() {
        KafkaConsumerResponse kafkaConsumerResponseStop = testRestTemplate.exchange(
            "http://localhost:" + port + "/api/admin/kafka/consumers/oppgaveEndret/stop",
            HttpMethod.POST,
            lagStringHttpEntity(),
            new ParameterizedTypeReference<KafkaConsumerResponse>() {
            }).getBody();

        assertThat(kafkaConsumerResponseStop).isNotNull();
        assertThat(kafkaConsumerResponseStop.isActive())
            .isFalse();

        KafkaConsumerResponse kafkaConsumerResponseStart = testRestTemplate.exchange(
            "http://localhost:" + port + "/api/admin/kafka/consumers/oppgaveEndret/start",
            HttpMethod.POST,
            lagStringHttpEntity(),
            new ParameterizedTypeReference<KafkaConsumerResponse>() {
            }).getBody();

        assertThat(kafkaConsumerResponseStart).isNotNull();
        assertThat(kafkaConsumerResponseStart.isActive())
            .isTrue();
    }

    @Test
    void settOffset_senderInnOffset2_consumerLeserPåNyttFraOffset2() throws Exception {
        final var rinaSaksnummer = Integer.toString(new Random().nextInt(100000));
        final var oppgaveID = Integer.toString(new Random().nextInt(100000));
        final var oppgaveID1 = Integer.toString(new Random().nextInt(100000));
        final var oppgaveID2 = Integer.toString(new Random().nextInt(100000));
        final var oppgaveID3 = Integer.toString(new Random().nextInt(100000));

        final var oppgaveDto = new HentOppgaveDto(oppgaveID, "AAPEN", 1);
        final var oppgaveDto1 = new HentOppgaveDto(oppgaveID1, "AAPEN", 1);
        final var oppgaveDto2 = new HentOppgaveDto(oppgaveID2, "AAPEN", 1);
        final var oppgaveDto3 = new HentOppgaveDto(oppgaveID3, "AAPEN", 1);

        when(oppgaveConsumer.hentOppgave(oppgaveID)).thenReturn(oppgaveDto);
        when(oppgaveConsumer.hentOppgave(oppgaveID1)).thenReturn(oppgaveDto1);
        when(oppgaveConsumer.hentOppgave(oppgaveID2)).thenReturn(oppgaveDto2);
        when(oppgaveConsumer.hentOppgave(oppgaveID3)).thenReturn(oppgaveDto3);

        String VERSJON = "1";
        kafkaTemplate.send(lagOppgaveIdentifisertRecord(oppgaveID, VERSJON, rinaSaksnummer)).get();
        kafkaTemplate.send(lagOppgaveIdentifisertRecord(oppgaveID1, VERSJON, rinaSaksnummer)).get();
        kafkaTemplate.send(lagOppgaveIdentifisertRecord(oppgaveID2, VERSJON, rinaSaksnummer)).get();
        kafkaTemplate.send(lagOppgaveIdentifisertRecord(oppgaveID3, VERSJON, rinaSaksnummer)).get();

        verify(oppgaveConsumer, timeout(1000).times(4)).hentOppgave(anyString());

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
            "http://localhost:" + port + "/api/admin/kafka/consumers/oppgaveEndret/seek/2",
            HttpMethod.POST,
            lagStringHttpEntity(),
            new ParameterizedTypeReference<>() {
            });

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Vi sender 4 meldinger, resetter til offset 2, melding med offset 2 og 3 leses på nytt, totalt hentes oppgave 6 ganger.
        verify(oppgaveConsumer, timeout(6_000).times(6)).hentOppgave(anyString());
    }

    @NotNull
    private HttpEntity<String> lagStringHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MELOSYS-ADMIN-APIKEY", "dummy");
        return new HttpEntity<>(headers);
    }
}
