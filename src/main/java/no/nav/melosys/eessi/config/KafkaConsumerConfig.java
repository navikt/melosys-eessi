package no.nav.melosys.eessi.config;

import java.util.HashMap;
import java.util.Map;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
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
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer2;
import org.springframework.kafka.support.serializer.JsonDeserializer;

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
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }

    private RecordFilterStrategy<String, SedHendelse> recordFilterStrategySedSendt() {
        // Return false to be dismissed
        return record -> !LEGISLATION_APPLICABLE_CODE.equalsIgnoreCase(record.value().getSektorKode());
    }

    private RecordFilterStrategy<String, SedHendelse> recordFilterStrategySedMottatt() {
        // Return false to be dismissed
        return record -> !LEGISLATION_APPLICABLE_CODE.equalsIgnoreCase(record.value().getSektorKode());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SedHendelse>> sedMottattListenerContainerFactory(
            KafkaProperties properties) {
        return sedListenerContainerFactory(properties, recordFilterStrategySedMottatt());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SedHendelse>> sedSendtListenerContainerFactory(
            KafkaProperties properties) {
        return sedListenerContainerFactory(properties, recordFilterStrategySedSendt());
    }

    private KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SedHendelse>> sedListenerContainerFactory(
            KafkaProperties properties, RecordFilterStrategy<String, SedHendelse> recordFilterStrategy) {
        Map<String, Object> props = properties.buildConsumerProperties();
        props.putAll(sedEventConsumerConfig());
        ErrorHandlingDeserializer2<SedHendelse> deserializer = valueDeserializer(SedHendelse.class);
        DefaultKafkaConsumerFactory<String, SedHendelse> defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(
                props, new StringDeserializer(), deserializer);
        ConcurrentKafkaListenerContainerFactory<String, SedHendelse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(defaultKafkaConsumerFactory);
        factory.setRecordFilterStrategy(recordFilterStrategy);
        //For replay of messages
        //factory.getContainerProperties().setAckOnError(false);
        //factory.getContainerProperties().setAckMode(AckMode.RECORD);
        return factory;
    }

    //ErrorHandlingDeserializer2 added to prevent never ending loop if parsing fails.
    private <T> ErrorHandlingDeserializer2<T> valueDeserializer(Class<T> targetType) {
        return new ErrorHandlingDeserializer2<>(new JsonDeserializer<>(targetType,false ));
    }
}