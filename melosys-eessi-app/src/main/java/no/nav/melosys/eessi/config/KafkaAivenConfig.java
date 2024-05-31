package no.nav.melosys.eessi.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.OppgaveKafkaAivenRecord;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.service.oppgave.OppgaveEndretService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.Message;

import static no.nav.melosys.eessi.identifisering.OppgaveKafkaAivenRecord.Hendelse.Hendelsestype.OPPGAVE_ENDRET;
import static no.nav.melosys.eessi.models.BucType.*;

@Configuration
@EnableKafka
@Slf4j
@Profile("!local-q2")
public class KafkaAivenConfig {

    private final Environment env;
    private final OppgaveEndretService oppgaveEndretService;
    private final SaksrelasjonService saksrelasjonService;

    @Value("${melosys.kafka.aiven.brokers}")
    private String brokersUrl;

    @Value("${melosys.kafka.aiven.keystorePath}")
    private String keystorePath;

    @Value("${melosys.kafka.aiven.truststorePath}")
    private String truststorePath;

    @Value("${melosys.kafka.aiven.credstorePassword}")
    private String credstorePassword;

    private static final String LEGISLATION_APPLICABLE_CODE_LA = "LA";

    public KafkaAivenConfig(Environment env, OppgaveEndretService oppgaveEndretService, SaksrelasjonService saksrelasjonService) {
        this.env = env;
        this.oppgaveEndretService = oppgaveEndretService;
        this.saksrelasjonService = saksrelasjonService;
    }

    @Bean
    public KafkaListenerErrorHandler sedMottattErrorHandler() {
        return (Message<?> message, ListenerExecutionFailedException exception) -> {
            log.error("Feil ved prosessering av Kafka-melding: sed_mottatt: {}\n{}", exception.getCause().getMessage(), message, exception);
            return null;
        };
    }

    @Bean
    public KafkaListenerErrorHandler oppgaveEndretErrorHandler() {
        return (Message<?> message, ListenerExecutionFailedException exception) -> {
            log.error("Feil ved prosessering av Kafka-melding: oppgave_endret: {}\n{}", exception.getCause().getMessage(), message, exception);
            return null;
        };
    }


    @Bean
    @Qualifier("aivenTemplate")
    public KafkaTemplate<String, Object> aivenKafkaTemplate(ObjectMapper objectMapper) {
        Map<String, Object> props = producerProps();
        ProducerFactory<String, Object> producerFactory =
            new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer<>(objectMapper));

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SedHendelse>> sedHendelseListenerContainerFactory(KafkaProperties kafkaProperties) {
        return sedListenerContainerFactory(kafkaProperties);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, OppgaveKafkaAivenRecord>> oppgaveEndretListenerContainerFactory(KafkaProperties kafkaProperties) {
        return oppgaveListenerContainerFactory(kafkaProperties);
    }

    private ConcurrentKafkaListenerContainerFactory<String, SedHendelse> sedListenerContainerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.putAll(consumerConfig());
        DefaultKafkaConsumerFactory<String, SedHendelse> defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(
            props, new StringDeserializer(), valueDeserializer(SedHendelse.class));
        ConcurrentKafkaListenerContainerFactory<String, SedHendelse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(defaultKafkaConsumerFactory);
        factory.setRecordFilterStrategy(recordFilterStrategySedListener());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        return factory;
    }

    private ConcurrentKafkaListenerContainerFactory<String, OppgaveKafkaAivenRecord> oppgaveListenerContainerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.putAll(consumerConfig());
        DefaultKafkaConsumerFactory<String, OppgaveKafkaAivenRecord> defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(
            props, new StringDeserializer(), valueDeserializer(OppgaveKafkaAivenRecord.class));
        ConcurrentKafkaListenerContainerFactory<String, OppgaveKafkaAivenRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(defaultKafkaConsumerFactory);
        factory.setRecordFilterStrategy(recordFilterStrategyOppgaveHendelserListener());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        return factory;
    }

    private Map<String, Object> producerProps() {
        Map<String, Object> props = new HashMap<>();

        props.put(CommonClientConfigs.CLIENT_ID_CONFIG, "eessi-producer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokersUrl);

        if (isNotLocal()) {
            props.putAll(securityConfig());
        }
        return props;
    }

    private Map<String, Object> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokersUrl);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);

        if (isNotLocal()) {
            props.putAll(securityConfig());
        }

        return props;
    }

    private Map<String, Object> securityConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");

        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststorePath);
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, credstorePassword);
        props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "JKS");

        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystorePath);
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, credstorePassword);
        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, credstorePassword);
        props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PKCS12");

        return props;
    }

    private <T> ErrorHandlingDeserializer<T> valueDeserializer(Class<T> targetType) {
        return new ErrorHandlingDeserializer<>(new JsonDeserializer<>(targetType, false));
    }

    private boolean isNotLocal() {
        return Arrays.stream(env.getActiveProfiles()).noneMatch(
            profile -> (profile.equalsIgnoreCase("local")
                || profile.equalsIgnoreCase("test")
                || profile.equalsIgnoreCase("local-mock"))
        );
    }

    private RecordFilterStrategy<String, SedHendelse> recordFilterStrategySedListener() {
        // Return false to be dismissed
        return consumerRecord -> !(
            LEGISLATION_APPLICABLE_CODE_LA.equalsIgnoreCase(consumerRecord.value().getSektorKode())
                || (skalHBucKonsumeres(consumerRecord.value().getBucType()) && erRinaSakIEessi(consumerRecord.value().getRinaSakId()))
        );
    }

    private boolean erRinaSakIEessi(String rinaSakId) {
        return saksrelasjonService.finnVedRinaSaksnummer(rinaSakId).isPresent();
    }

    private RecordFilterStrategy<String, OppgaveKafkaAivenRecord> recordFilterStrategyOppgaveHendelserListener() {
        return consumerRecord -> !(
            OPPGAVE_ENDRET.equals(consumerRecord.value().hendelse().hendelsestype())
                && oppgaveEndretService.erIdentifiseringsOppgave(consumerRecord.value())
        );
    }
}
