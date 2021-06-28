package no.nav.melosys.eessi.kafka.consumers;

import lombok.extern.slf4j.Slf4j;
import no.finn.unleash.Unleash;
import no.nav.melosys.eessi.metrikker.SedMetrikker;
import no.nav.melosys.eessi.models.SedMottatt;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.service.behandling.BehandleSedMottattService;
import no.nav.melosys.eessi.service.mottak.SedMottakService;
import no.nav.melosys.eessi.service.sed.SedMottattService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.config.MDCLogging.loggSedID;
import static no.nav.melosys.eessi.config.MDCLogging.slettSedIDLogging;

@Slf4j
@Component
public class SedMottattConsumer {

    private final SedMottattService sedMottattService;
    private final SedMottakService sedMottakService;
    private final BehandleSedMottattService behandleSedMottattService;
    private final SedMetrikker sedMetrikker;
    private final Unleash unleash;

    @Autowired
    public SedMottattConsumer(SedMottattService sedMottattService,
                              SedMottakService sedMottakService,
                              BehandleSedMottattService behandleSedMottattService,
                              SedMetrikker sedMetrikker,
                              Unleash unleash) {
        this.sedMottattService = sedMottattService;
        this.sedMottakService = sedMottakService;
        this.behandleSedMottattService = behandleSedMottattService;
        this.sedMetrikker = sedMetrikker;
        this.unleash = unleash;
    }

    @KafkaListener(clientIdPrefix = "melosys-eessi-sedMottatt",
            topics = "${melosys.kafka.consumer.mottatt.topic}", containerFactory = "sedMottattListenerContainerFactory")
    public void sedMottatt(ConsumerRecord<String, SedHendelse> consumerRecord) {
        log.info("Mottatt melding om sed mottatt: {}, offset: {}", consumerRecord.value(), consumerRecord.offset());
        loggSedID(consumerRecord.value().getSedId());

        if (unleash.isEnabled("melosys.eessi.en_identifisering_oppg")) {
            sedMottakService.behandleSed(SedMottattHendelse.builder()
                    .sedHendelse(consumerRecord.value())
                    .build());
        } else {
            behandleMottatt(SedMottatt.av(consumerRecord.value()));
        }

        sedMetrikker.sedMottatt(consumerRecord.value().getSedType());
        slettSedIDLogging();

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
