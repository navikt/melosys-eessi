package no.nav.melosys.eessi.kafka.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.eessi.basis.SedMottatt;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SedMottattConsumer {

    @KafkaListener(clientIdPrefix = "melosys-eessi-sedMottatt", topics = "privat-eessi-basis-sedMottatt-v1",
            containerFactory = "sedMottattListenerContainerFactory")
    public void sedMottatt(ConsumerRecord<String, SedMottatt> consumerRecord) {
        SedMottatt sedMottatt = consumerRecord.value();
        log.info("Sed mottatt: {}", sedMottatt);
    }
}
