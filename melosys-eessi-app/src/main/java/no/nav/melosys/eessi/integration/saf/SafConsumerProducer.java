package no.nav.melosys.eessi.integration.saf;

import no.nav.melosys.eessi.integration.interceptor.CorrelationIdOutgoingInterceptor;
import no.nav.melosys.eessi.integration.pdl.PDLConsumer;
import no.nav.melosys.eessi.security.GenericAuthFilterFactory;
import no.nav.melosys.eessi.security.SystemContextSafClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Collections;

@Configuration
public class SafConsumerProducer {

    @Bean
    public SafConsumer safConsumer(WebClient.Builder webclientBuilder,
                                   @Value("${melosys.integrations.saf-url}") String safUrl,
                                   GenericAuthFilterFactory genericAuthFilterFactory
    ) {
        return new SafConsumer(
            webclientBuilder
                .baseUrl(safUrl)
                .defaultHeaders(this::defaultHeaders)
                .filter(genericAuthFilterFactory.getAzureFilter("saf"))
                .build()
        );
    }

    private void defaultHeaders(HttpHeaders httpHeaders) {
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }
}
