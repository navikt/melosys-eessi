package no.nav.melosys.eessi.integration.pdl;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PDLConsumerProducer {

    @Bean
    public PDLConsumer pdlConsumer(WebClient.Builder webclientBuilder,
                                   @Value("${melosys.integrations.pdl-url}") String pdlUrl,
                                   PDLSystemAuthFilter pdlSystemAuthFilter) {
        return new PDLConsumer(
                webclientBuilder
                        .baseUrl(pdlUrl)
                        .defaultHeaders(this::defaultHeaders)
                        .filter(pdlSystemAuthFilter)
                        .build()
        );
    }

    private void defaultHeaders(HttpHeaders httpHeaders) {
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Tema", "MED");
    }
}
