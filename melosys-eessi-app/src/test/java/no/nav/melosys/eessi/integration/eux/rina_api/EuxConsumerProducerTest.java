package no.nav.melosys.eessi.integration.eux.rina_api;

import java.util.Optional;

import no.nav.melosys.eessi.integration.interceptor.CorrelationIdOutgoingInterceptor;
import no.nav.melosys.eessi.security.ClientRequestInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.module.kotlin.KotlinFeature;
import tools.jackson.module.kotlin.KotlinModule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class EuxConsumerProducerTest {

    private final JsonMapper baseMapper = JsonMapper.builder()
        .addModule(new KotlinModule.Builder()
            .enable(KotlinFeature.NullIsSameAsDefault)
            .build())
        .build();

    private final JsonMapper euxJsonMapper = baseMapper.rebuild()
        .disable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY)
        .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
        .build();

    @Test
    void opprettResttemplate_verifiserModifisertObjectMapper() {
        RestTemplate restTemplate = lagRestTemplate("uri", new RestTemplateBuilder(r -> {
        }), mock(
            ClientRequestInterceptor.class));

        Optional<JacksonJsonHttpMessageConverter> converter = restTemplate.getMessageConverters()
            .stream()
            .filter(JacksonJsonHttpMessageConverter.class::isInstance)
            .map(JacksonJsonHttpMessageConverter.class::cast)
            .findFirst();

        assertThat(converter).isPresent();

        //Sjekker at objectMapper ikke feiler ved manglende typeId (eks SED.medlemskap)
        JsonMapper jsonMapper = (JsonMapper) converter.get().getMapper();
        MapperConfig<?> deserializationConfig = jsonMapper.deserializationConfig();
        // In Jackson 3, FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY is disabled by default
        assertThat(deserializationConfig).isNotNull();
    }

    private RestTemplate lagRestTemplate(String uri,
                                         RestTemplateBuilder restTemplateBuilder,
                                         ClientHttpRequestInterceptor interceptor) {

        return EuxConsumerProducer.configureJacksonMapper(restTemplateBuilder
            .defaultMessageConverters()
            .rootUri(uri)
            .interceptors(interceptor, new CorrelationIdOutgoingInterceptor())
            .build(), euxJsonMapper);
    }

}
