package no.nav.melosys.eessi.kafka.producers;

import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Slf4j
@Service
public class MelosysEessiProducer {

    private final KafkaTemplate<String, MelosysEessiMelding> kafkaTemplate;
    private final String topicName;

    public MelosysEessiProducer(KafkaTemplate<String, MelosysEessiMelding> kafkaTemplate, @Value("${melosys.kafka.producer.topic-name}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void publiserMelding(MelosysEessiMelding melding) {
        ListenableFuture<SendResult<String, MelosysEessiMelding>> future = kafkaTemplate.send(topicName, melding);

        try {
            future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IntegrationException("Feil ved publisering av melding på kafka", e);
        } catch (ExecutionException e) {
            throw new IntegrationException("Feil ved publisering av melding på kafka", e);
        }
    }
}
