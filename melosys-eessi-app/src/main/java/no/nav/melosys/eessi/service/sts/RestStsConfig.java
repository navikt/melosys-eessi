package no.nav.melosys.eessi.service.sts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestStsConfig {

    private final String uri;

    public RestStsConfig(@Value("${melosys.integrations.reststs-url}") String uri) {
        this.uri = uri;
    }

    @Bean(name = "restStsRestTemplate")
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
            .rootUri(uri)
            .build();
    }
}
