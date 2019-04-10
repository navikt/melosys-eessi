package no.nav.melosys.eessi.integration.tps.aktoer;

import no.nav.melosys.eessi.security.BasicAuthClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class AktoerConsumerProducer {

    private final String url;

    public AktoerConsumerProducer(@Value("${melosys.integrations.aktoer-url}") String url) {
        this.url = url;
    }

    @Bean
    public AktoerConsumer aktoerConsumer(BasicAuthClientRequestInterceptor basicAuthClientRequestInterceptor) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .interceptors(basicAuthClientRequestInterceptor)
                .build();
        return new AktoerConsumer(restTemplate);
    }
}
