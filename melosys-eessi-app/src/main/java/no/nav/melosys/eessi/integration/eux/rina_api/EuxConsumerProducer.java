package no.nav.melosys.eessi.integration.eux.rina_api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.nav.melosys.eessi.integration.interceptor.CorrelationIdOutgoingInterceptor;
import no.nav.melosys.eessi.security.ClientRequestInterceptor;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EuxConsumerProducer {

    private final String uri;

    public EuxConsumerProducer(@Value("${melosys.integrations.euxapp-url}") String uri) {
        this.uri = uri;
    }

    @Bean
    @Primary
    public EuxConsumer euxConsumer(RestTemplateBuilder builder, ClientConfigurationProperties clientConfigurationProperties,
                                   OAuth2AccessTokenService oAuth2AccessTokenService, ObjectMapper objectMapper, Environment environment) {
        ClientRequestInterceptor interceptor = new ClientRequestInterceptor(clientConfigurationProperties, oAuth2AccessTokenService, "eux-rina-api");
        return new EuxConsumer(lagRestTemplate(builder, interceptor), objectMapper, environment);
    }

    private RestTemplate lagRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                         ClientHttpRequestInterceptor interceptor) {
        RestTemplate restTemplate = restTemplateBuilder
            .defaultMessageConverters()
            .rootUri(uri)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .interceptors(interceptor, new CorrelationIdOutgoingInterceptor())
            .build();

        return configureJacksonMapper(restTemplate);
    }

    public static RestTemplate configureJacksonMapper(RestTemplate restTemplate) {
        //For å kunne ta i mot SED'er som ikke har et 'medlemskap' objekt, eks X001
        restTemplate.getMessageConverters().stream()
            .filter(MappingJackson2HttpMessageConverter.class::isInstance)
            .map(MappingJackson2HttpMessageConverter.class::cast)
            .findFirst().ifPresent(jacksonConverer ->
                jacksonConverer.getObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false)
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
            );

        return restTemplate;
    }
}
