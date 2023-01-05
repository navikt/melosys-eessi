package no.nav.melosys.eessi.kafka.consumers;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    private final Set<Long> alreadyAddedMetrics = ConcurrentHashMap.newKeySet();

    @Autowired
    public SedMottattConsumer(SedMottakService sedMottakService, SedMetrikker sedMetrikker) {
        this.sedMottakService = sedMottakService;
        this.sedMetrikker = sedMetrikker;
    }

    @KafkaListener(clientIdPrefix = "melosys-eessi-sedMottatt",
        topics = "${melosys.kafka.aiven.consumer.mottatt.topic}",
        containerFactory = "sedHendelseListenerContainerFactory",
        groupId = "${melosys.kafka.aiven.consumer.mottatt.groupid}",
        errorHandler = "sedMottattErrorHandler"
    )
    public void sedMottatt(ConsumerRecord<String, SedHendelse> consumerRecord) {
        SedHendelse sedHendelse = consumerRecord.value();
        putToMDC(SED_ID, sedHendelse.getSedId());
        putToMDC(CORRELATION_ID, UUID.randomUUID().toString());

        log.info("Mottatt melding om sed mottatt: {}, offset: {}", sedHendelse, consumerRecord.offset());

        try {
            sedMottakService.behandleSed(SedMottattHendelse.builder()
                .sedHendelse(sedHendelse)
                .build());

            sedMetrikker.sedMottatt(sedHendelse.getSedType());
        } catch (SedAlleredeJournalførtException e) {
            log.info("SED {} allerede journalført", e.getSedID());
            logMetrikker(consumerRecord.offset(), sedHendelse.getSedType(), e);
        } catch (Exception e) {
            logMetrikker(consumerRecord.offset(), sedHendelse.getSedType(), e);
            log.error("sedMottatt av {} feilet med feilet med: {}", sedHendelse.getSedType(), e.getMessage());
            throw e;
        } finally {
            remove(SED_ID);
            remove(CORRELATION_ID);
        }
    }

    private void logMetrikker(long offset, String sedType, Exception e) {
        if (alreadyAddedMetrics.add(offset)) {
            // Unngå å skrive metrikker for 10 retry som kjøres av kafka
            sedMetrikker.sedMottattFeilet(sedType, e);
        }
    }
}
