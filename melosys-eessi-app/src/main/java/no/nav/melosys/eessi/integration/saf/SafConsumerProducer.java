package no.nav.melosys.eessi.integration.saf;

import java.util.Collections;

import no.nav.melosys.eessi.security.GenericAuthFilterFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SafConsumerProducer {

    @Bean
    public SafConsumer safConsumer(WebClient.Builder webclientBuilder,
                                   @Value("${melosys.integrations.saf-url}") String safUrl,
                                   GenericAuthFilterFactory genericAuthFilterFactory
    ) {
        // Setter buffer til 100MB for å håndtere store dokumenter
        ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs((ClientCodecConfigurer configurer) ->
                configurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024))
            .build();

        return new SafConsumer(
            webclientBuilder
                .baseUrl(safUrl)
                .defaultHeaders(this::defaultHeaders)
                .filter(genericAuthFilterFactory.getAzureFilter("saf"))
                .exchangeStrategies(strategies)
                .build()
        );
    }

    private void defaultHeaders(HttpHeaders httpHeaders) {
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }
}
