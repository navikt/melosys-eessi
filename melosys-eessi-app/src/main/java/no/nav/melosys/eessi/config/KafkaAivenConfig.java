package no.nav.melosys.eessi.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableKafka
public class KafkaAivenConfig {

    @Autowired
    private Environment env;

    @Value("${melosys.kafka.aiven.brokers}")
    private String brokersUrl;

    @Value("${melosys.kafka.aiven.keystorePath}")
    private String keystorePath;

    @Value("${melosys.kafka.aiven.truststorePath}")
    private String truststorePath;

    @Value("${melosys.kafka.aiven.credstorePassword}")
    private String credstorePassword;

    @Bean
    @Qualifier("aivenTemplate")
    public KafkaTemplate<String, Object> aivenKafkaTemplate(ObjectMapper objectMapper) {
        Map<String, Object> props = producerProps();
        ProducerFactory<String, Object> producerFactory =
            new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer<>(objectMapper));

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SedHendelse>>
    aivenSedHendelseListenerContainerFactory(
        KafkaProperties kafkaProperties, @Value("${melosys.kafka.aiven.consumer.groupid}") String groupId) {
        return kafkaListenerContainerFactory(SedHendelse.class, kafkaProperties, groupId);
    }

    public <T> ConcurrentKafkaListenerContainerFactory<String, T> kafkaListenerContainerFactory(
        Class<T> containerType, KafkaProperties kafkaProperties, String groupId) {

        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.putAll(consumerConfig(groupId));
        DefaultKafkaConsumerFactory<String, T> defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(
            props, new StringDeserializer(), valueDeserializer(containerType));
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(defaultKafkaConsumerFactory);

        return factory;
    }

    private Map<String, Object> producerProps() {
        Map<String, Object> props = new HashMap<>();

        props.put(CommonClientConfigs.CLIENT_ID_CONFIG, "eessi-producer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokersUrl);

        if (isNotLocal()) {
            props.putAll(nonLocalSecurityConfig());
        }
        return props;
    }

    private Map<String, Object> consumerConfig(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokersUrl);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);

        if (isNotLocal()) {
            props.putAll(nonLocalSecurityConfig());
        }

        return props;
    }

    private Map<String, Object> nonLocalSecurityConfig() {
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
            profile -> (profile.equalsIgnoreCase("local") || profile.equalsIgnoreCase("test"))
        );
    }
}
