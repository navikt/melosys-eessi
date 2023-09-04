package no.nav.melosys.eessi.integration.eux.rina_api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.nav.melosys.eessi.integration.interceptor.CorrelationIdOutgoingInterceptor;
import no.nav.melosys.eessi.security.SystemContextClientRequestInterceptor;
import no.nav.melosys.eessi.security.UserContextEuxClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
    public EuxConsumer euxConsumer(RestTemplateBuilder builder, SystemContextClientRequestInterceptor interceptor, ObjectMapper objectMapper) {
        return new EuxConsumer(euxRestTemplate(builder, interceptor), objectMapper);
    }

    @Bean
    @Qualifier("tokenContext")
    public EuxConsumer euxTokenContextConsumer(RestTemplateBuilder builder, UserContextEuxClientRequestInterceptor interceptor, ObjectMapper objectMapper) {
        return new EuxConsumer(euxTokenContextRestTemplate(builder, interceptor), objectMapper);
    }

    protected RestTemplate euxRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                     SystemContextClientRequestInterceptor interceptor) {
        return lagRestTemplate(restTemplateBuilder, interceptor);
    }

    private RestTemplate euxTokenContextRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                                     UserContextEuxClientRequestInterceptor interceptor) {
        return lagRestTemplate(restTemplateBuilder, interceptor);
    }

    private RestTemplate lagRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                         ClientHttpRequestInterceptor interceptor) {
        RestTemplate restTemplate =  restTemplateBuilder
                .defaultMessageConverters()
                .rootUri(uri)
                .interceptors(interceptor, new CorrelationIdOutgoingInterceptor())
                .build();

        return configureJacksonMapper(restTemplate);
    }

    private static RestTemplate configureJacksonMapper(RestTemplate restTemplate) {
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
