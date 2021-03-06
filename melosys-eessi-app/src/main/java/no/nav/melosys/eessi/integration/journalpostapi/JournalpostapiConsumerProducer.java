package no.nav.melosys.eessi.integration.journalpostapi;

import no.nav.melosys.eessi.security.SystemContextClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class JournalpostapiConsumerProducer {

    private final String url;

    public JournalpostapiConsumerProducer(@Value("${melosys.integrations.journalpostapi-url}") String url) {
        this.url = url;
    }

    @Bean
    public JournalpostapiConsumer journalpostapiConsumer(
            SystemContextClientRequestInterceptor systemContextClientRequestInterceptor) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .interceptors(systemContextClientRequestInterceptor)
                .build();

        return new JournalpostapiConsumer(restTemplate);
    }

}
