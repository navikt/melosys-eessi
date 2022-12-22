package no.nav.melosys.eessi.kafka.consumers;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.journalpostapi.SedAlleredeJournalførtException;
import no.nav.melosys.eessi.metrikker.SedMetrikker;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.service.mottak.SedMottakService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.config.MDCOperations.*;

@Slf4j
@Component
public class SedMottattConsumer {

    private final SedMottakService sedMottakService;
    private final SedMetrikker sedMetrikker;

    @Autowired
    public SedMottattConsumer(SedMottakService sedMottakService, SedMetrikker sedMetrikker) {
        this.sedMottakService = sedMottakService;
        this.sedMetrikker = sedMetrikker;
    }

    @KafkaListener(clientIdPrefix = "melosys-eessi-sedMottatt",
        topics = "${melosys.kafka.aiven.consumer.mottatt.topic}",
        containerFactory = "sedHendelseListenerContainerFactory")
    public void sedMottatt(ConsumerRecord<String, SedHendelse> consumerRecord) {
        putToMDC(SED_ID, consumerRecord.value().getSedId());
        putToMDC(CORRELATION_ID, UUID.randomUUID().toString());

        log.info("Mottatt melding om sed mottatt: {}, offset: {}", consumerRecord.value(), consumerRecord.offset());

        try {
            sedMottakService.behandleSed(SedMottattHendelse.builder()
                .sedHendelse(consumerRecord.value())
                .build());

            sedMetrikker.sedMottatt(consumerRecord.value().getSedType());
        } catch (SedAlleredeJournalførtException e) {
            log.info("SED {} allerede journalført", e.getSedID());
        } catch (Exception e) {
            // TODO: lag metrikker på feilet exception
            log.error("sedMottatt feilet med: {}", e.getMessage());
            throw e;
        } finally {
            remove(SED_ID);
            remove(CORRELATION_ID);
        }
    }
}
