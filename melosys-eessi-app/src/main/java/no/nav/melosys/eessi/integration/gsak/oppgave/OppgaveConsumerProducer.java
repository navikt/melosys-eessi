package no.nav.melosys.eessi.integration.gsak.oppgave;

import no.nav.melosys.eessi.security.BasicAuthClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class OppgaveConsumerProducer {

    private final String url;

    public OppgaveConsumerProducer(@Value("${melosys.integrations.gsak.oppgave-url}") String url) {
        this.url = url;
    }

    @Bean
    public OppgaveConsumer oppgaveConsumer(BasicAuthClientRequestInterceptor basicAuthClientRequestInterceptor) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .interceptors(basicAuthClientRequestInterceptor)
                .build();
        return new OppgaveConsumer(restTemplate);
    }
}
