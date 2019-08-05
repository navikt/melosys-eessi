package no.nav.melosys.eessi.integration.dokkat;

import no.nav.melosys.eessi.security.BasicAuthClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class DokkatConsumerProducer {

    private final String dokTypeIdUrl;
    private final String dokTypeInfoUrl;

    public DokkatConsumerProducer(@Value("${melosys.integrations.dokkat.typeid-url}") String dokTypeIdUrl,
                                  @Value("${melosys.integrations.dokkat.typeinfo-url}") String dokTypeInfoUrl) {
        this.dokTypeIdUrl = dokTypeIdUrl;
        this.dokTypeInfoUrl = dokTypeInfoUrl;
    }

    @Bean
    public DokumenttypeIdConsumer dokumenttypeIdRestClient(
            BasicAuthClientRequestInterceptor basicAuthClientRequestInterceptor) {

        RestTemplate restTemplate = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(dokTypeIdUrl))
                .interceptors(basicAuthClientRequestInterceptor)
                .build();

        return new DokumenttypeIdConsumer(restTemplate);
    }

    @Bean
    public DokumenttypeInfoConsumer dokumenttypeInfoRestClient(
            BasicAuthClientRequestInterceptor basicAuthClientRequestInterceptor) {

        RestTemplate restTemplate = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(dokTypeInfoUrl))
                .interceptors(basicAuthClientRequestInterceptor)
                .build();

        return new DokumenttypeInfoConsumer(restTemplate);
    }
}
