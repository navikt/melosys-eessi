package no.nav.melosys.eessi.integration.eux.rina_api;

import tools.jackson.databind.json.JsonMapper;
import no.nav.melosys.eessi.integration.interceptor.CorrelationIdOutgoingInterceptor;
import no.nav.melosys.eessi.security.ClientRequestInterceptor;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EuxConsumerProducer {

    private final String uri;

    public EuxConsumerProducer(@Value("${melosys.integrations.eux-rina-api-url}") String uri) {
        this.uri = uri + "/cpi";
    }

    @Bean
    @Primary
    public EuxConsumer euxConsumer(RestTemplateBuilder builder, ClientConfigurationProperties clientConfigurationProperties,
                                   OAuth2AccessTokenService oAuth2AccessTokenService,
                                   @Qualifier("euxJsonMapper") JsonMapper euxJsonMapper,
                                   Environment environment) {
        ClientRequestInterceptor interceptor = new ClientRequestInterceptor(clientConfigurationProperties, oAuth2AccessTokenService, "eux-rina-api");
        return new EuxConsumer(lagRestTemplate(builder, interceptor, euxJsonMapper), euxJsonMapper, environment);
    }

    /**
     * Konfigurerer RestTemplate med EUX-spesifikk JsonMapper.
     * Brukes av tester som trenger å sette opp RestTemplate manuelt.
     */
    public static RestTemplate configureJacksonMapper(RestTemplate restTemplate, JsonMapper euxJsonMapper) {
        restTemplate.getMessageConverters().removeIf(JacksonJsonHttpMessageConverter.class::isInstance);
        restTemplate.getMessageConverters().add(new JacksonJsonHttpMessageConverter(euxJsonMapper));
        return restTemplate;
    }

    private RestTemplate lagRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                         ClientHttpRequestInterceptor interceptor,
                                         JsonMapper jsonMapper) {
        RestTemplate restTemplate = restTemplateBuilder
            .defaultMessageConverters()
            .rootUri(uri)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .interceptors(interceptor, new CorrelationIdOutgoingInterceptor())
            .build();

        return configureJacksonMapper(restTemplate, jsonMapper);
    }
}
