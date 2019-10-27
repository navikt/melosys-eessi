package no.nav.melosys.eessi.kafka.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.metrikker.SedMetrikker;
import no.nav.melosys.eessi.models.SedMottatt;
import no.nav.melosys.eessi.service.behandling.BehandleSedMottattService;
import no.nav.melosys.eessi.service.sed.SedMottattService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SedMottattConsumer {

    private final SedMottattService sedMottattService;
    private final BehandleSedMottattService behandleSedMottattService;
    private final SedMetrikker sedMetrikker;

    @Autowired
    public SedMottattConsumer(SedMottattService sedMottattService,
            BehandleSedMottattService behandleSedMottattService,
            SedMetrikker sedMetrikker) {
        this.sedMottattService = sedMottattService;
        this.behandleSedMottattService = behandleSedMottattService;
        this.sedMetrikker = sedMetrikker;
    }

    @KafkaListener(containerFactory = "sedMottattListenerContainerFactory",
            topics = "eessi-basis-sedMottatt-v1", clientIdPrefix = "melosys-eessi-sedMottatt")
    public void sedMottatt(ConsumerRecord<String, SedHendelse> consumerRecord) {
        SedMottatt sedMottatt = SedMottatt.av(consumerRecord.value());

        log.info("Mottatt melding om sed mottatt: {}, offset: {}", sedMottatt.getSedHendelse(), consumerRecord.offset());
        behandleMottatt(sedMottatt);
        sedMetrikker.sedMottatt(sedMottatt.getSedHendelse().getSedType());
    }

    private void behandleMottatt(SedMottatt sedMottatt) {
        try {
            behandleSedMottattService.behandleSed(sedMottatt);
        } catch (Exception e) {
            log.error("Feil i behandling av mottatt sed. Lagres for å prøve igjen senere", e);
            sedMottatt.setFeiledeForsok(sedMottatt.getFeiledeForsok() + 1);
            sedMottattService.lagre(sedMottatt);
        }
    }
}
