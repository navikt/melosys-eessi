package no.nav.melosys.eessi.integration.dokarkivsed;

import no.nav.melosys.eessi.security.OidcTokenClientrequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class DokarkivSedConsumerProducer {

    private final String url;

    public DokarkivSedConsumerProducer(@Value("${melosys.integrations.dokarkivsed-url}") String url) {
        this.url = url;
    }

    @Bean
    public DokarkivSedConsumer dokarkivSedConsumer(OidcTokenClientrequestInterceptor oidcTokenClientrequestInterceptor) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .interceptors(oidcTokenClientrequestInterceptor)
                .build();

        return new DokarkivSedConsumer(restTemplate);
    }
}
