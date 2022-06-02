package no.nav.melosys.eessi.integration.journalpostapi;

import no.nav.melosys.eessi.security.SystemContextClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.Duration;

@Configuration
public class JournalpostapiConsumerProducer {

    private final String url;
    private static final int CONNECT_TIMEOUT_SECONDS = 60;
    private static final int READ_TIMEOUT_SECONDS = 60;

    public JournalpostapiConsumerProducer(@Value("${melosys.integrations.journalpostapi-url}") String url) {
        this.url = url;
    }

    @Bean
    public JournalpostapiConsumer journalpostapiConsumer(
            SystemContextClientRequestInterceptor systemContextClientRequestInterceptor) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .interceptors(systemContextClientRequestInterceptor)
                .setConnectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                .setReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS))
                .build();

        return new JournalpostapiConsumer(restTemplate);
    }

}
