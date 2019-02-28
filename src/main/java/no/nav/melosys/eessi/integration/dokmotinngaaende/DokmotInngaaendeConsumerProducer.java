package no.nav.melosys.eessi.integration.dokmotinngaaende;

import no.nav.melosys.eessi.security.OidcTokenClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class DokmotInngaaendeConsumerProducer {

    private final String url;

    public DokmotInngaaendeConsumerProducer(@Value("${melosys.integrations.dokmotinngaaende-url}") String url) {
        this.url = url;
    }

    @Bean
    public DokmotInngaaendeConsumer dokmotInngaaendeConsumer(OidcTokenClientRequestInterceptor oidcTokenClientRequestInterceptor) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .interceptors(oidcTokenClientRequestInterceptor)
                .build();

        return new DokmotInngaaendeConsumer(restTemplate);
    }
}
