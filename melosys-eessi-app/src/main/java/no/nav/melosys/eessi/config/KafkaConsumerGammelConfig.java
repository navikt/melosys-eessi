package no.nav.melosys.eessi.config;

import java.util.HashMap;
import java.util.Map;

import no.nav.melosys.eessi.identifisering.OppgaveEndretHendelseGammel;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
public class KafkaConsumerGammelConfig {

    private final String groupId;

    public KafkaConsumerGammelConfig(@Value("${melosys.kafka.consumer.oppgave-endret.groupid}") String groupId) {
        this.groupId = groupId;
    }

    private Map<String, Object> consumerProperties(String offsetResetConfig) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetResetConfig);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        return props;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, OppgaveEndretHendelseGammel>> oppgaveListenerContainerFactory(
            KafkaProperties properties) {
        Map<String, Object> props = properties.buildConsumerProperties();
        props.putAll(consumerProperties("earliest"));
        DefaultKafkaConsumerFactory<String, OppgaveEndretHendelseGammel> defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(
                props, new StringDeserializer(), valueDeserializer(OppgaveEndretHendelseGammel.class));
        ConcurrentKafkaListenerContainerFactory<String, OppgaveEndretHendelseGammel> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(defaultKafkaConsumerFactory);
        factory.setErrorHandler(new SeekToCurrentErrorHandler());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    private <T> ErrorHandlingDeserializer<T> valueDeserializer(Class<T> targetType) {
        return new ErrorHandlingDeserializer<>(new JsonDeserializer<>(targetType,false ));
    }
}
