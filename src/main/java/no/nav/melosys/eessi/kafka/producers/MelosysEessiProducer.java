package no.nav.melosys.eessi.kafka.producers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

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

        future.addCallback(new ListenableFutureCallback<SendResult<String, MelosysEessiMelding>>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("Kunne ikke sende melding om ny behandling: {}", melding, throwable); //TODO: store and retry
            }

            @Override
            public void onSuccess(SendResult<String, MelosysEessiMelding> res) {
                log.info("Melding sendt på topic {}. Record.key: {}, record: {}",
                        topicName, res.getProducerRecord().key(), res.getProducerRecord().value());
            }
        });
    }
}
