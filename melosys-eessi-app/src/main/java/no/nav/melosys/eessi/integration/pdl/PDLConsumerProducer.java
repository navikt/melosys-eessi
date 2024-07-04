package no.nav.melosys.eessi.integration.pdl;

import java.util.Collections;

import no.nav.melosys.eessi.security.GenericAuthFilterFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PDLConsumerProducer {

    private static final String BEHANDLINGSNUMMER = "behandlingsnummer";
    private static final String MELOSYS_EESSI_BEHANDLINGSNUMMER = "B358";

    @Bean
    public PDLConsumer pdlConsumer(WebClient.Builder webclientBuilder,
                                   @Value("${melosys.integrations.pdl-url}") String pdlUrl,
                                   GenericAuthFilterFactory genericAuthFilterFactory
    ) {
        return new PDLConsumer(
            webclientBuilder
                .baseUrl(pdlUrl)
                .defaultHeaders(this::defaultHeaders)
                .filter(genericAuthFilterFactory.getAzureFilter("pdl"))
                .build()
        );
    }

    private void defaultHeaders(HttpHeaders httpHeaders) {
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(BEHANDLINGSNUMMER, MELOSYS_EESSI_BEHANDLINGSNUMMER);
    }
}
