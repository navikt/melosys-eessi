package no.nav.melosys.eessi.integration.pdl;

import no.nav.melosys.eessi.integration.WebClientConfig;
import no.nav.melosys.eessi.security.PDLWebContextExchangeFilter;
import no.nav.melosys.eessi.security.SystemContextClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class PdlWebConsumerProducer implements WebClientConfig {

    private final String uri;
    private final PDLWebContextExchangeFilter pdlWebContextExchangeFilter;

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
                .filter(errorFilter("Feil ved kall til PDL Web"))
                .build()
        );
    }
}
