package no.nav.melosys.eessi.integration.pdl;

import java.util.Collections;

import no.nav.melosys.eessi.config.MDCOperations;
import no.nav.melosys.eessi.integration.WebClientConfig;
import no.nav.melosys.eessi.security.GenericAuthFilterFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class PdlWebConsumerProducer implements WebClientConfig {
    private static final String BEHANDLINGSNUMMER = "behandlingsnummer";
    private static final String MELOSYS_EESSI_BEHANDLINGSNUMMER = "B358";

    @Bean
    public PdlWebConsumer pdlWebConsumer(WebClient.Builder webclientBuilder,
                                         @Value("${melosys.integrations.pdl-web-url}") String pdlWebUrl,
                                         GenericAuthFilterFactory genericAuthFilterFactory
    ) {
        return new PdlWebConsumer(
            webclientBuilder
                .baseUrl(pdlWebUrl)
                .defaultHeaders(this::defaultHeaders)
                .filter(genericAuthFilterFactory.getAzureFilter("pdl-web"))
                .build()
        );
    }

    private void defaultHeaders(HttpHeaders httpHeaders) {
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(BEHANDLINGSNUMMER, MELOSYS_EESSI_BEHANDLINGSNUMMER);

        if (MDCOperations.getCorrelationId() != null) {
            httpHeaders.add(MDCOperations.X_CORRELATION_ID, MDCOperations.getCorrelationId());
        }
    }
}
