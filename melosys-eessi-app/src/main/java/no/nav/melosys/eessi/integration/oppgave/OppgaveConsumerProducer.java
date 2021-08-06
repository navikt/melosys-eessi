package no.nav.melosys.eessi.integration.oppgave;

import java.util.Collections;

import no.nav.melosys.eessi.security.SystemContextRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OppgaveConsumerProducer {

    private final String url;

    public OppgaveConsumerProducer(@Value("${melosys.integrations.gsak.oppgave-url}") String url) {
        this.url = url;
    }

    @Bean
    public OppgaveConsumer oppgaveConsumer(WebClient.Builder webClientBuilder, SystemContextRequestFilter systemContextRequestFilter) {
        return new OppgaveConsumer(
                webClientBuilder
                        .baseUrl(url)
                        .defaultHeaders(this::defaultHeaders)
                        .filter(systemContextRequestFilter)
                        .build()
        );
    }

    private void defaultHeaders(HttpHeaders httpHeaders) {
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }
}
