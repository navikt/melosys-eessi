package no.nav.melosys.eessi.integration.eux.rina_api;

import no.nav.melosys.eessi.integration.interceptor.CorrelationIdOutgoingInterceptor;
import no.nav.melosys.eessi.security.ClientRequestInterceptor;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class EuxRinasakerConsumerProducer {

    private final String uri;

    public EuxRinasakerConsumerProducer(@Value("${melosys.integrations.euxapp-rinasaker-url}") String uri) {
        this.uri = uri;
    }

    @Bean
    @Primary
    public EuxRinasakerConsumer euxRinasakerConsumer(RestTemplateBuilder builder, ClientConfigurationProperties
        clientConfigurationProperties,
                                                     OAuth2AccessTokenService oAuth2AccessTokenService,
                                                     JsonMapper euxJsonMapper) {
        ClientRequestInterceptor interceptor = new ClientRequestInterceptor(clientConfigurationProperties, oAuth2AccessTokenService, "eux-nav-rinasak");
        return new EuxRinasakerConsumer(lagRestTemplate(builder, interceptor, euxJsonMapper));
    }

    private RestTemplate lagRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                         ClientHttpRequestInterceptor interceptor,
                                         JsonMapper jsonMapper) {
        RestTemplate restTemplate = restTemplateBuilder
            .defaultMessageConverters()
            .rootUri(uri)
            .interceptors(interceptor, new CorrelationIdOutgoingInterceptor())
            .build();

        restTemplate.getMessageConverters().removeIf(JacksonJsonHttpMessageConverter.class::isInstance);
        restTemplate.getMessageConverters().add(new JacksonJsonHttpMessageConverter(jsonMapper));

        return restTemplate;
    }
}
