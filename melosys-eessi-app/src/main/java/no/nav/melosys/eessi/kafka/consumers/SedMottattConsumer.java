package no.nav.melosys.eessi.kafka.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.service.behandling.BehandleSedMottattService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SedMottattConsumer {

    private final BehandleSedMottattService behandleSedMottattService;

    @Autowired
    public SedMottattConsumer(BehandleSedMottattService behandleSedMottattService) {
        this.behandleSedMottattService = behandleSedMottattService;
    }

    @KafkaListener(clientIdPrefix = "melosys-eessi-sedMottatt", topics = "eessi-basis-sedMottatt-v1",
            containerFactory = "sedMottattListenerContainerFactory")
    public void sedMottatt(ConsumerRecord<String, SedHendelse> consumerRecord) {
        SedHendelse sedMottatt = consumerRecord.value();

        log.info("Sed mottatt: {}", sedMottatt);
        behandleSedMottattService.behandleSed(sedMottatt);
    }
}
