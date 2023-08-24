package no.nav.melosys.eessi.integration.pdl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer;
import no.nav.melosys.eessi.integration.interceptor.CorrelationIdOutgoingInterceptor;
import no.nav.melosys.eessi.integration.oppgave.OppgaveConsumer;
import no.nav.melosys.eessi.security.SystemContextClientRequestInterceptor;
import no.nav.melosys.eessi.security.SystemContextRequestFilter;
import no.nav.melosys.eessi.security.UserContextEuxClientRequestInterceptor;
import no.nav.melosys.eessi.security.UserContextPDLClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;


@Configuration
public class PDLRestConsumerProducer {

    private final String uri;

    public PDLRestConsumerProducer(@Value("${melosys.integrations.pdl-web-url}") String uri) {
        this.uri = uri;
    }

    @Bean
    @Primary
    public PDLRestConsumer pdlRestConsumer(RestTemplateBuilder builder, SystemContextClientRequestInterceptor interceptor) {
        return new PDLRestConsumer(pdlRestTemplate(builder, interceptor));
    }

    @Bean
    @Qualifier("tokenContext")
    public PDLRestConsumer pdlTokenContextConsumer(RestTemplateBuilder builder, UserContextPDLClientRequestInterceptor interceptor) {
        return new PDLRestConsumer(pdlTokenContextRestTemplate(builder, interceptor));
    }

    protected RestTemplate pdlRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                           SystemContextClientRequestInterceptor interceptor) {
        return lagRestTemplate(restTemplateBuilder, interceptor);
    }

    private RestTemplate pdlTokenContextRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                                     UserContextPDLClientRequestInterceptor interceptor) {
        return lagRestTemplate(restTemplateBuilder, interceptor);
    }

    private RestTemplate lagRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                         ClientHttpRequestInterceptor interceptor) {

        return restTemplateBuilder
            .defaultMessageConverters()
            .rootUri(uri)
            .interceptors(interceptor, new CorrelationIdOutgoingInterceptor())
            .build();
    }
}
