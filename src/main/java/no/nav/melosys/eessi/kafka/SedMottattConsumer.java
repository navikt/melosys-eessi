package no.nav.melosys.eessi.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.eessi.basis.SedMottatt;
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

    @KafkaListener(clientIdPrefix = "melosys-eessi-sedMottatt", topics = "privat-eessi-basis-sedMottatt-v1",
            containerFactory = "sedMottattListenerContainerFactory")
    public void sedMottatt(ConsumerRecord<String, SedMottatt> consumerRecord) {
        SedMottatt sedMottatt = consumerRecord.value();

        log.info("Sed mottatt med rinaSakId: {}", sedMottatt.getRinaSakId());
        behandleSedMottattService.behandleSed(sedMottatt);
    }
}
