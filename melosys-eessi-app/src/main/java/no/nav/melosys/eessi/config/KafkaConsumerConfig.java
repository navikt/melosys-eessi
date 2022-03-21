package no.nav.melosys.eessi.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.melosys.eessi.identifisering.OppgaveEndretHendelse;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.metrikker.MetrikkerNavn;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.LoggerFactory;
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
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    private static final String LEGISLATION_APPLICABLE_CODE = "LA";

    private final String groupId;
    private static final Counter SED_SENDT_JFR_FEILET_COUNTER = Metrics.counter(MetrikkerNavn.METRIKKER_NAMESPACE + "sed-mottatt-feil");

    public KafkaConsumerConfig(@Value("${melosys.kafka.consumer.groupid}") String groupId) {
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

    private RecordFilterStrategy<String, SedHendelse> recordFilterStrategySedSendt() {
        // Return false to be dismissed
        return consumerRecord -> !LEGISLATION_APPLICABLE_CODE.equalsIgnoreCase(consumerRecord.value().getSektorKode());
    }

    private RecordFilterStrategy<String, SedHendelse> recordFilterStrategySedMottatt() {
        // Return false to be dismissed
        return consumerRecord -> !LEGISLATION_APPLICABLE_CODE.equalsIgnoreCase(consumerRecord.value().getSektorKode());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SedHendelse>> sedMottattListenerContainerFactory(
            KafkaProperties properties) {
        return sedListenerContainerFactory(properties, recordFilterStrategySedMottatt(), new SeekToCurrentErrorHandler());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SedHendelse>> sedSendtListenerContainerFactory(
            KafkaProperties properties
    ) {
        return sedListenerContainerFactory(
            properties,
            recordFilterStrategySedSendt(),
            new SeekToCurrentErrorHandler(sedSendtRecoverer(), new FixedBackOff(5000L, 9L))
        );
    }

    private BiConsumer<ConsumerRecord<?, ?>, Exception> sedSendtRecoverer() {
        return (consumerRecord, ex) -> {
            var logger = LoggerFactory.getLogger(this.getClass());
            if (consumerRecord.value() instanceof SedHendelse sedHendelse) {
                logger.error("Kan ikke behandle SED {}", sedHendelse.getSedId());
                SED_SENDT_JFR_FEILET_COUNTER.increment();
            } else {
                logger.error("Feil ved prosessering av melding {}", consumerRecord.value());
            }
        };
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, OppgaveEndretHendelse>> oppgaveListenerContainerFactory(
            KafkaProperties properties) {
        Map<String, Object> props = properties.buildConsumerProperties();
        props.putAll(consumerProperties("earliest"));
        DefaultKafkaConsumerFactory<String, OppgaveEndretHendelse> defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(
                props, new StringDeserializer(), valueDeserializer(OppgaveEndretHendelse.class));
        ConcurrentKafkaListenerContainerFactory<String, OppgaveEndretHendelse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(defaultKafkaConsumerFactory);
        factory.setErrorHandler(new SeekToCurrentErrorHandler());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    private KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SedHendelse>> sedListenerContainerFactory(
            KafkaProperties properties,
            RecordFilterStrategy<String, SedHendelse> recordFilterStrategy,
            ErrorHandler errorHandler
    ) {
        Map<String, Object> props = properties.buildConsumerProperties();
        props.putAll(consumerProperties("earliest"));
        DefaultKafkaConsumerFactory<String, SedHendelse> defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(
                props, new StringDeserializer(), valueDeserializer(SedHendelse.class));
        ConcurrentKafkaListenerContainerFactory<String, SedHendelse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(defaultKafkaConsumerFactory);
        factory.setErrorHandler(errorHandler);
        factory.setRecordFilterStrategy(recordFilterStrategy);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    private <T> ErrorHandlingDeserializer<T> valueDeserializer(Class<T> targetType) {
        return new ErrorHandlingDeserializer<>(new JsonDeserializer<>(targetType,false ));
    }
}
