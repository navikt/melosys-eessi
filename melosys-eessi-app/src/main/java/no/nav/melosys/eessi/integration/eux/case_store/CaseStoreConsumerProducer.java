package no.nav.melosys.eessi.integration.eux.case_store;

import no.nav.melosys.eessi.integration.interceptor.CorrelationIdOutgoingInterceptor;
import no.nav.melosys.eessi.security.RestSTSInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Konfigurasjon for å sette opp en RestTemplate for å kommunisere med CaseStore
 */
@Deprecated
@Configuration
public class CaseStoreConsumerProducer {

    private final String url;

    public CaseStoreConsumerProducer(@Value("${melosys.integrations.eux-case-store-url}") String url) {
        this.url = url;
    }

    @Bean("caseStoreResttemplate")
    public RestTemplate caseStoreResttemplate(RestTemplateBuilder restTemplateBuilder,
                                              RestSTSInterceptor requestInterceptor,
                                              CorrelationIdOutgoingInterceptor correlationIdOutgoingInterceptor) {
        return restTemplateBuilder
            .defaultMessageConverters()
            .rootUri(url)
            .interceptors(requestInterceptor, correlationIdOutgoingInterceptor)
            .build();
    }
}
