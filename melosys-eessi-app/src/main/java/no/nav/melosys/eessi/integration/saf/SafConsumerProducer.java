package no.nav.melosys.eessi.integration.saf;

import no.nav.melosys.eessi.controller.interceptor.CorrelationIdOutgoingInterceptor;
import no.nav.melosys.eessi.security.SystemContextClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class SafConsumerProducer {

    private final String uri;

    public SafConsumerProducer(@Value("${melosys.integrations.saf-url}") String uri) {
        this.uri = uri;
    }

    @Bean
    public RestTemplate safRestTemplate(SystemContextClientRequestInterceptor interceptor,
                                        CorrelationIdOutgoingInterceptor correlationIdOutgoingInterceptor) {
        return new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(uri))
                .interceptors(interceptor, correlationIdOutgoingInterceptor)
                .build();
    }

}
