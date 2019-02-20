package no.nav.melosys.eessi.integration.eux;

import no.nav.melosys.eessi.security.OidcTokenClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EuxConsumerConfig {

    private final String uri;

    public EuxConsumerConfig(@Value("${melosys.integrations.euxapp-url}") String uri) {
        this.uri = uri;
    }

    @Bean(name = "euxRestTemplate")
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder,
            OidcTokenClientRequestInterceptor oidcTokenClientrequestInterceptor) {

        return restTemplateBuilder
                .rootUri(uri)
                .interceptors(oidcTokenClientrequestInterceptor)
                .build();
    }
}
