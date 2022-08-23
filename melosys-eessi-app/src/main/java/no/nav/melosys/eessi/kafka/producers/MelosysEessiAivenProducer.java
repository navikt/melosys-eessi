package no.nav.melosys.eessi.kafka.producers;

import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static no.nav.melosys.eessi.config.MDCOperations.CORRELATION_ID;
import static no.nav.melosys.eessi.config.MDCOperations.getCorrelationId;

@Slf4j
@Service
public class MelosysEessiAivenProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topicName;

    public MelosysEessiAivenProducer(@Qualifier("aivenTemplate") KafkaTemplate<String, Object> kafkaTemplate,
                                     @Value("${melosys.kafka.aiven.producer.aiven-topic-name}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void publiserMelding(MelosysEessiMelding melding) {
        try {
            ProducerRecord<String, Object> melosysEessiRecord = new ProducerRecord<>(topicName, melding);
            melosysEessiRecord.headers().add(CORRELATION_ID, getCorrelationId().getBytes());
            kafkaTemplate.send(melosysEessiRecord).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IntegrationException("Feil ved publisering av melding på aiven kafka", e);
        } catch (ExecutionException e) {
            throw new IntegrationException("Feil ved publisering av melding på aiven kafka", e);
        }
    }
}
