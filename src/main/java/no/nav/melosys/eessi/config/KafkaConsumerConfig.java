package no.nav.melosys.eessi.config;

import java.util.HashMap;
import java.util.Map;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import no.nav.eessi.basis.SedMottatt;
import no.nav.eessi.basis.SedSendt;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    private static final String LEGISLATION_APPLICABLE_CODE = "LA";

    @Bean
    public Map<String, Object> sedEventConsumerConfig() {
        Map<String, Object> props = new HashMap<>();
        //Without this, the consumer will receive GenericData records.
        props.put("specific.avro.reader", true);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "melosys-eessi-sedHendelser");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 100);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        return props;
    }

    @Bean
    public RecordFilterStrategy<String, SedSendt> recordFilterStrategySedSendt() {
        // Return false to be dismissed
        return record -> !LEGISLATION_APPLICABLE_CODE.equalsIgnoreCase(record.value().getSektorKode());
    }

    @Bean
    public RecordFilterStrategy<String, SedMottatt> recordFilterStrategySedMottatt() {
        // Return false to be dismissed
        return record -> !LEGISLATION_APPLICABLE_CODE.equalsIgnoreCase(record.value().getSektorKode());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SedSendt>> sedSendtListenerContainerFactory(
        KafkaProperties properties) {
        Map<String, Object> props = properties.buildConsumerProperties();
        props.putAll(sedEventConsumerConfig());
        DefaultKafkaConsumerFactory<String, SedSendt> defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(props);
        ConcurrentKafkaListenerContainerFactory<String, SedSendt> factory;
        factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setRecordFilterStrategy(recordFilterStrategySedSendt());
        factory.setConsumerFactory(defaultKafkaConsumerFactory);
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SedMottatt>> sedMottattListenerContainerFactory(
            KafkaProperties properties) {
        Map<String, Object> props = properties.buildConsumerProperties();
        props.putAll(sedEventConsumerConfig());
        DefaultKafkaConsumerFactory<String, SedMottatt> defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(props);
        ConcurrentKafkaListenerContainerFactory<String, SedMottatt> factory;
        factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setRecordFilterStrategy(recordFilterStrategySedMottatt());
        factory.setConsumerFactory(defaultKafkaConsumerFactory);
        return factory;
    }
}