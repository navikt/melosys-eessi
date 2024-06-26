package no.nav.melosys.eessi.integration.eux.rina_api;

import java.util.Optional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.integration.interceptor.CorrelationIdOutgoingInterceptor;
import no.nav.melosys.eessi.security.ClientRequestInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class EuxConsumerProducerTest {

    @Test
    void opprettResttemplate_verifiserModifisertObjectMapper() {
        RestTemplate restTemplate = lagRestTemplate("uri", new RestTemplateBuilder(r -> {
        }), mock(
            ClientRequestInterceptor.class));

        Optional<MappingJackson2HttpMessageConverter> converter = restTemplate.getMessageConverters()
            .stream()
            .filter(MappingJackson2HttpMessageConverter.class::isInstance)
            .map(MappingJackson2HttpMessageConverter.class::cast)
            .findFirst();

        assertThat(converter).isPresent();

        //Sjekker at objectMapper ikke feiler ved manglende typeId (eks SED.medlemskap)
        ObjectMapper objectMapper = converter.get().getObjectMapper();
        assertThat(objectMapper
            .getDeserializationConfig()
            .hasDeserializationFeatures(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY.getMask())
        ).isFalse();
    }

    private RestTemplate lagRestTemplate(String uri,
                                         RestTemplateBuilder restTemplateBuilder,
                                         ClientHttpRequestInterceptor interceptor) {

        return EuxConsumerProducer.configureJacksonMapper(restTemplateBuilder
            .defaultMessageConverters()
            .rootUri(uri)
            .interceptors(interceptor, new CorrelationIdOutgoingInterceptor())
            .build());
    }

}
