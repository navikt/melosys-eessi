package no.nav.melosys.eessi.integration.pdl;

import no.nav.melosys.eessi.config.MDCOperations;
import no.nav.melosys.eessi.integration.WebClientConfig;
import no.nav.melosys.eessi.security.PDLWebContextExchangeFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class PdlWebConsumerProducer implements WebClientConfig {

    private final String uri;
    private final PDLWebContextExchangeFilter pdlWebContextExchangeFilter;
    private static final String BEHANDLINGSNUMMER = "behandlingsnummer";
    private static final String MELOSYS_EESSI_BEHANDLINGSNUMMER = "B358";

    public PdlWebConsumerProducer(@Value("${melosys.integrations.pdl-web-url}") String uri,
                                  PDLWebContextExchangeFilter pdlWebContextExchangeFilter) {
        this.uri = uri;
        this.pdlWebContextExchangeFilter = pdlWebContextExchangeFilter;
    }

    @Bean
    @Primary
    public PdlWebConsumer pdlWebConsumer(WebClient.Builder webClientBuilder) {
        return new PdlWebConsumer(
            webClientBuilder
                .baseUrl(uri)
                .filter(pdlWebContextExchangeFilter)
                .defaultHeaders(this::defaultHeaders)
                .filter(errorFilter("Feil ved kall til PDL Web"))
                .build()
        );
    }

    private void defaultHeaders(HttpHeaders httpHeaders) {
        httpHeaders.set(BEHANDLINGSNUMMER, MELOSYS_EESSI_BEHANDLINGSNUMMER);

        if (MDCOperations.getCorrelationId() != null) {
            httpHeaders.add(MDCOperations.X_CORRELATION_ID, MDCOperations.getCorrelationId());
        }
    }

}
