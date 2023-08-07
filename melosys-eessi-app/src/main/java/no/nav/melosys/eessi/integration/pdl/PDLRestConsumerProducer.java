package no.nav.melosys.eessi.integration.pdl;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.oppgave.OppgaveConsumer;
import no.nav.melosys.eessi.security.SystemContextRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

@Slf4j
@Configuration
public class PDLRestConsumerProducer {

    private final String url;

    public PDLRestConsumerProducer(@Value("${melosys.integrations.pdl-web-url}") String url) {
        this.url = url;
    }

    @Bean
    public PDLRestConsumer pdlRestConsumer(WebClient.Builder webClientBuilder, PDLSystemAuthFilter pdlSystemAuthFilter) {

        log.info("[EESSI TEST] pdlSystemAuthFilter: {}", pdlSystemAuthFilter);

        return new PDLRestConsumer(
                webClientBuilder
                        .baseUrl(url)
                        .defaultHeaders(this::defaultHeaders)
                        .filter(pdlSystemAuthFilter)
                        .build()
        );
    }

    private void defaultHeaders(HttpHeaders httpHeaders) {
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }
}
