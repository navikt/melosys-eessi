package no.nav.melosys.eessi.integration;

import no.nav.melosys.eessi.config.AppCredentials;
import no.nav.melosys.eessi.integration.interceptor.CorrelationIdOutgoingInterceptor;
import no.nav.melosys.eessi.security.BasicAuthClientRequestInterceptor;
import no.nav.melosys.eessi.security.SystemContextClientRequestInterceptor;
import no.nav.melosys.eessi.service.sts.RestStsClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class IntegrationTestConfiguration {

    @Bean
    public SystemContextClientRequestInterceptor systemContextClientRequestInterceptor() {
        return new SystemContextClientRequestInterceptor(new TestRestSTSClient());
    }

    @Bean
    public CorrelationIdOutgoingInterceptor correlationIdOutgoingInterceptor() {
        return new CorrelationIdOutgoingInterceptor();
    }

    @Bean
    public BasicAuthClientRequestInterceptor basicAuthClientRequestInterceptor(){
        AppCredentials appCredentials = new AppCredentials();
        appCredentials.setPassword("123");
        appCredentials.setUsername("dummy");
        return new BasicAuthClientRequestInterceptor(appCredentials);
    }

    class TestRestSTSClient implements RestStsClient {

        @Override
        public String bearerToken() {
            return null;
        }

        @Override
        public String collectToken() {
            return null;
        }
    }
}
