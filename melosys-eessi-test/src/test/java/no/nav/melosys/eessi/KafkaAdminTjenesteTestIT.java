package no.nav.melosys.eessi;

import java.util.List;

import no.nav.melosys.eessi.controller.dto.KafkaConsumerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class KafkaAdminTjenesteTestIT extends ComponentTestBase{

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void getKafkaConsumers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MELOSYS-ADMIN-APIKEY", "dummy");
        HttpEntity<String> entity = new HttpEntity<>( headers);

        List<KafkaConsumerResponse> kafkaConsumerResponses = testRestTemplate.exchange(
            "http://localhost:" + port + "/api/admin/kafka/consumers",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<List<KafkaConsumerResponse>>() {
            }).getBody();

        assertThat(kafkaConsumerResponses)
            .hasSize(4);
    }

    @Test
    void stopAndStartConsumerById() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MELOSYS-ADMIN-APIKEY", "dummy");
        HttpEntity<String> entity = new HttpEntity<>( headers);

        KafkaConsumerResponse kafkaConsumerResponseStop = testRestTemplate.exchange(
            "http://localhost:" + port + "/api/admin/kafka/consumers/oppgaveEndret/stop",
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<KafkaConsumerResponse>() {
            }).getBody();

        assertThat(kafkaConsumerResponseStop.getActive())
            .isNotNull()
            .isFalse();

        KafkaConsumerResponse kafkaConsumerResponseStart = testRestTemplate.exchange(
            "http://localhost:" + port + "/api/admin/kafka/consumers/oppgaveEndret/start",
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<KafkaConsumerResponse>() {
            }).getBody();

        assertThat(kafkaConsumerResponseStart.getActive())
            .isNotNull()
            .isTrue();
    }
}
