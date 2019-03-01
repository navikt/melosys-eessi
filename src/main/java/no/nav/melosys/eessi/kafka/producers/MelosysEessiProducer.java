package no.nav.melosys.eessi.kafka.producers;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.avro.MelosysEessiMelding;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Service
public class MelosysEessiProducer {

    private final KafkaTemplate<String, MelosysEessiMelding> kafkaTemplate;
    private static final String TOPIC_NAME = "privat-melosys-eessi-v1";

    public MelosysEessiProducer(KafkaTemplate<String, MelosysEessiMelding> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publiserMelding(MelosysEessiMelding melding) {
        ListenableFuture<SendResult<String, MelosysEessiMelding>> future = kafkaTemplate.send(TOPIC_NAME, melding);

        future.addCallback(new ListenableFutureCallback<SendResult<String, MelosysEessiMelding>>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("Kunne ikke sende melding om ny behandling: {}", melding, throwable); //TODO: store and retry
            }

            @Override
            public void onSuccess(SendResult<String, MelosysEessiMelding> res) {
                log.info("Melding sendt på topic {}: {}", TOPIC_NAME, res.getProducerRecord().value());
            }
        });
    }
}
