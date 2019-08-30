package no.nav.melosys.eessi.integration.gsak.sak;

import no.nav.melosys.eessi.security.BasicAuthClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class SakConsumerProducer {

    private final String url;

    public SakConsumerProducer(@Value("${melosys.integrations.gsak.sak-url}") String url) {
        this.url = url;
    }

    @Bean
    public SakConsumer sakRestClient(BasicAuthClientRequestInterceptor basicAuthClientRequestInterceptor) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .interceptors(basicAuthClientRequestInterceptor)
                .build();
        return new SakConsumer(restTemplate);
    }
}
