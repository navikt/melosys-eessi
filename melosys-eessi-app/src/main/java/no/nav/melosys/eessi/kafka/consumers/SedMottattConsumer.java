package no.nav.melosys.eessi.kafka.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.metrikker.SedMetrikker;
import no.nav.melosys.eessi.models.SedMottatt;
import no.nav.melosys.eessi.service.behandling.BehandleSedMottattGammelService;
import no.nav.melosys.eessi.service.behandling.BehandleSedMottattService;
import no.nav.melosys.eessi.service.sed.SedMottattService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.config.MDCLogging.loggSedID;
import static no.nav.melosys.eessi.config.MDCLogging.slettSedIDLogging;

@Slf4j
@Component
public class SedMottattConsumer {

    private final SedMottattService sedMottattService;
    private final BehandleSedMottattService behandleSedMottattService;
    private final BehandleSedMottattGammelService behandleSedMottattGammelService;
    private final SedMetrikker sedMetrikker;
    private final boolean brukNySedMottatService;

    @Autowired
    public SedMottattConsumer(SedMottattService sedMottattService,
                              BehandleSedMottattService behandleSedMottattService,
                              BehandleSedMottattGammelService behandleSedMottattGammelService,
                              SedMetrikker sedMetrikker,
                              @Value("${melosys.feature.nyttMottak}") String brukNySedMottattService) {
        this.sedMottattService = sedMottattService;
        this.behandleSedMottattService = behandleSedMottattService;
        this.behandleSedMottattGammelService = behandleSedMottattGammelService;
        this.sedMetrikker = sedMetrikker;
        this.brukNySedMottatService = "true".equals(brukNySedMottattService);
    }

    @KafkaListener(clientIdPrefix = "melosys-eessi-sedMottatt",
            topics = "${melosys.kafka.consumer.mottatt.topic}", containerFactory = "sedMottattListenerContainerFactory")
    public void sedMottatt(ConsumerRecord<String, SedHendelse> consumerRecord) {
        SedMottatt sedMottatt = SedMottatt.av(consumerRecord.value());

        log.info("Mottatt melding om sed mottatt: {}, offset: {}", sedMottatt.getSedHendelse(), consumerRecord.offset());
        behandleMottatt(sedMottatt);
        sedMetrikker.sedMottatt(sedMottatt.getSedHendelse().getSedType());
    }

    private void behandleMottatt(SedMottatt sedMottatt) {
        try {
            loggSedID(sedMottatt.getSedHendelse().getSedId());
            if(brukNySedMottatService) {
                behandleSedMottattService.behandleSed(sedMottatt);
            } else {
                behandleSedMottattGammelService.behandleSed(sedMottatt);
            }
        } catch (Exception e) {
            log.error("Feil i behandling av mottatt sed. Lagres for å prøve igjen senere", e);
            sedMottatt.setFeiledeForsok(sedMottatt.getFeiledeForsok() + 1);
            sedMottattService.lagre(sedMottatt);
        } finally {
            slettSedIDLogging();
        }
    }
}
