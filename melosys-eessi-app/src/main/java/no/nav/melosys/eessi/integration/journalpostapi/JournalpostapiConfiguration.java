package no.nav.melosys.eessi.integration.journalpostapi;

import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.integration.interceptor.CorrelationIdOutgoingInterceptor;
import no.nav.melosys.eessi.security.RestSTSInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class JournalpostapiConfiguration {

    private final String url;
    private static final int CONNECT_TIMEOUT_SECONDS = 60;
    private static final int READ_TIMEOUT_SECONDS = 60;

    public JournalpostapiConfiguration(@Value("${melosys.integrations.journalpostapi-url}") String url) {
        this.url = url;
    }

    @Bean
    public RestTemplate journalpostapiRestTemplate(
        RestTemplateBuilder restTemplateBuilder,
        RestSTSInterceptor systemContextClientRequestInterceptor,
        CorrelationIdOutgoingInterceptor correlationIdOutgoingInterceptor) {
        return restTemplateBuilder
            .requestFactory(SimpleClientHttpRequestFactory::new)
            .uriTemplateHandler(new DefaultUriBuilderFactory(url))
            .interceptors(systemContextClientRequestInterceptor, correlationIdOutgoingInterceptor)
            .setConnectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
            .setReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS))
            .setBufferRequestBody(false)
            .build();
    }

    @Bean
    public JournalpostapiConsumer journalpostapiConsumer(
        RestTemplate journalpostapiRestTemplate,
        ObjectMapper objectMapper) {

        return new JournalpostapiConsumer(journalpostapiRestTemplate, objectMapper);
    }

}
