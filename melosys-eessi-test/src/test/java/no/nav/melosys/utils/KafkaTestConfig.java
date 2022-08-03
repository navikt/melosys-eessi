package no.nav.melosys.utils;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Slf4j
@TestConfiguration
@ComponentScan
@ConditionalOnClass({
        KafkaListener.class,
        Aspect.class,
        ConsumerRecord.class
})
@EnableConfigurationProperties(KafkaTestProperties.class)
public class KafkaTestConfig {

    @Bean("testKafkaListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<Object, Object> testKafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            KafkaProperties properties,
            KafkaTestProperties testProperties) throws ClassNotFoundException {

        log.info("Configuring KafkaTestConsumer for Topic Pattern: " + testProperties.getTopicsPattern());
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, consumerFactory(properties, testProperties));
        factory.setConcurrency(1);
        return factory;
    }

    private ConsumerFactory<Object, Object> consumerFactory(KafkaProperties properties, KafkaTestProperties testProperties) throws ClassNotFoundException {
        Map<String, Object> merged = properties.buildConsumerProperties();

        merged.put("client-id", "test");
        merged.put("enable.auto.commit", testProperties.getEnableAutoCommit());
        merged.put("key.deserializer", Class.forName(testProperties.getKeyDeserializer()));
        merged.put("value.deserializer", Class.forName(testProperties.getValueDeserializer()));
        merged.put("isolation.level", testProperties.getIsolationLevel());

        return new DefaultKafkaConsumerFactory<>(merged);
    }
}
