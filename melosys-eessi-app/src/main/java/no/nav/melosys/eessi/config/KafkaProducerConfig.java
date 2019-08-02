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
    
    @Autowired
    private ProducerFactory<Object, Object> producerFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void melosysEessiProducerFactory() {
        JsonSerializer<Object> jsonSerializer = new JsonSerializer<>(objectMapper);

        ((DefaultKafkaProducerFactory)producerFactory).setValueSerializer(jsonSerializer);
    }
}
