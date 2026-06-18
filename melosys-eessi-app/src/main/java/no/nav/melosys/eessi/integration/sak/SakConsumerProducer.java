package no.nav.melosys.eessi.integration.sak;

import java.util.Collections;

import no.nav.melosys.eessi.security.GenericAuthFilterFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SakConsumerProducer {

    private final String url;

    public SakConsumerProducer(@Value("${melosys.integrations.gsak.sak-url}") String url) {
        this.url = url;
    }

    @Bean
    public SakConsumer sakRestClient(WebClient.Builder webClientBuilder,
                                     GenericAuthFilterFactory genericAuthFilterFactory) {
        return new SakConsumer(
            webClientBuilder
                .baseUrl(url)
                .defaultHeaders(this::defaultHeaders)
                .filter(genericAuthFilterFactory.getAzureFilter("sak"))
                .build()
        );
    }

    private void defaultHeaders(HttpHeaders httpHeaders) {
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }
}
