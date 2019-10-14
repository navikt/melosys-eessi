package no.nav.melosys.eessi.integration.eux.rina_api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import no.nav.melosys.eessi.security.OidcTokenClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EuxConsumerConfig {

    private final String uri;

    public EuxConsumerConfig(@Value("${melosys.integrations.euxapp-url}") String uri) {
        this.uri = uri;
    }

    @Bean(name = "euxRestTemplate")
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder,
            OidcTokenClientRequestInterceptor oidcTokenClientrequestInterceptor) {

        RestTemplate restTemplate =  restTemplateBuilder
                .defaultMessageConverters()
                .rootUri(uri)
                .interceptors(oidcTokenClientrequestInterceptor)
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
                );

        return restTemplate;
    }
}
