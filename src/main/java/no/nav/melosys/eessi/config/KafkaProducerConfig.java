package no.nav.melosys.eessi.config;

import java.util.Map;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import no.nav.melosys.eessi.avro.MelosysEessiBehandling;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, MelosysEessiBehandling> melosysEessiProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildProducerProperties();
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, MelosysEessiBehandling> kafkaTemplate(
            ProducerFactory<String, MelosysEessiBehandling> melosysEessiProducerFactory) {
        return new KafkaTemplate<>(melosysEessiProducerFactory);
    }
}
