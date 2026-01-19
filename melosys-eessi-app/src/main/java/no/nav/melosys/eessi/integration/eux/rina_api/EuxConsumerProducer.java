package no.nav.melosys.eessi.integration.eux.rina_api;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;
import no.nav.melosys.eessi.integration.interceptor.CorrelationIdOutgoingInterceptor;
import no.nav.melosys.eessi.security.ClientRequestInterceptor;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
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
                                   OAuth2AccessTokenService oAuth2AccessTokenService, JsonMapper jsonMapper, Environment environment) {
        ClientRequestInterceptor interceptor = new ClientRequestInterceptor(clientConfigurationProperties, oAuth2AccessTokenService, "eux-rina-api");
        JsonMapper euxMapper = createEuxMapper(jsonMapper);
        return new EuxConsumer(lagRestTemplate(builder, interceptor, euxMapper), euxMapper, environment);
    }

    private static JsonMapper createEuxMapper(JsonMapper baseMapper) {
        // Bygg ny mapper basert på global konfig, men med EUX-spesifikke innstillinger
        // For å kunne ta i mot SED'er som ikke har et 'medlemskap' objekt, eks X001
        return baseMapper.rebuild()
            .disable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY)
            .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();
    }

    /**
     * Konfigurerer RestTemplate med EUX-spesifikk JsonMapper.
     * Brukes av tester som trenger å sette opp RestTemplate manuelt.
     */
    public static RestTemplate configureJacksonMapper(RestTemplate restTemplate, JsonMapper baseMapper) {
        JsonMapper euxMapper = createEuxMapper(baseMapper);
        restTemplate.getMessageConverters().removeIf(JacksonJsonHttpMessageConverter.class::isInstance);
        restTemplate.getMessageConverters().add(new JacksonJsonHttpMessageConverter(euxMapper));
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

        restTemplate.getMessageConverters().removeIf(JacksonJsonHttpMessageConverter.class::isInstance);
        restTemplate.getMessageConverters().add(new JacksonJsonHttpMessageConverter(jsonMapper));

        return restTemplate;
    }
}
