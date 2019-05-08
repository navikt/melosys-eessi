package no.nav.melosys.eessi.config;

import no.nav.melosys.eessi.kafka.producers.MelosysEessiMelding;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;



@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, MelosysEessiMelding> melosysEessiProducerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties(), new StringSerializer(), new JsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<String, MelosysEessiMelding> kafkaTemplate(
            ProducerFactory<String, MelosysEessiMelding> melosysEessiProducerFactory) {
        return new KafkaTemplate<>(melosysEessiProducerFactory);
    }
}
