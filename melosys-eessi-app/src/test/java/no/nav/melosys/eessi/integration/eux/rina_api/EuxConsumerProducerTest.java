package no.nav.melosys.eessi.integration.eux.rina_api;

import java.util.Optional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.security.SystemContextClientRequestInterceptor;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class EuxConsumerProducerTest {

    @Test
    public void opprettResttemplate_verifiserModifisertObjectMapper() {
        EuxConsumerProducer euxConsumerProducer = new EuxConsumerProducer("uri");
        RestTemplate restTemplate = euxConsumerProducer.euxRestTemplate(new RestTemplateBuilder(r -> {}), mock(
                SystemContextClientRequestInterceptor.class));

        Optional<MappingJackson2HttpMessageConverter> converter = restTemplate.getMessageConverters()
                .stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast)
                .findFirst();

        assertThat(converter.isPresent()).isTrue();

        //Sjekker at objectMapper ikke feiler ved manglende typeId (eks SED.medlemskap)
        ObjectMapper objectMapper = converter.get().getObjectMapper();
        assertThat(
                objectMapper.getDeserializationConfig()
                        .hasDeserializationFeatures(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY.getMask())
        ).isFalse();
    }
}
