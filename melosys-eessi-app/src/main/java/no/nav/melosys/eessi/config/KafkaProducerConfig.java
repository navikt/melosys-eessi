package no.nav.melosys.eessi.config;

import javax.annotation.PostConstruct;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {

    private final ProducerFactory<Object, Object> producerFactory;

    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaProducerConfig(final ProducerFactory<Object, Object> producerFactory, final ObjectMapper objectMapper) {
        this.producerFactory = producerFactory;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void melosysEessiProducerFactory() {
        JsonSerializer<Object> jsonSerializer = new JsonSerializer<>(objectMapper);

        ((DefaultKafkaProducerFactory)producerFactory).setValueSerializer(jsonSerializer);
    }
}
