package no.nav.melosys.eessi.kafka.producers;

import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MelosysEessiAivenProducer {

    private final KafkaTemplate<String, MelosysEessiMelding> kafkaTemplate;
    private final String topicName;

    public MelosysEessiAivenProducer(@Qualifier("eessiMelding") KafkaTemplate<String, MelosysEessiMelding> kafkaTemplate,
                                     @Value("${melosys.kafka.aiven.producer.aiven-topic-name}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void publiserMelding(MelosysEessiMelding melding) {
        try {
            kafkaTemplate.send(topicName, melding).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IntegrationException("Feil ved publisering av melding på aiven kafka", e);
        } catch (ExecutionException e) {
            throw new IntegrationException("Feil ved publisering av melding på aiven kafka", e);
        }
    }
}
