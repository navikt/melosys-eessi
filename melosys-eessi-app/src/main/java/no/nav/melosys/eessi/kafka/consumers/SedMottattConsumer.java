// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.kafka.consumers;

import java.util.UUID;
import no.nav.melosys.eessi.integration.journalpostapi.SedAlleredeJournalførtException;
import no.nav.melosys.eessi.metrikker.SedMetrikker;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.service.kafkadlq.KafkaDLQService;
import no.nav.melosys.eessi.service.mottak.SedMottakService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AbstractConsumerSeekAware;
import org.springframework.stereotype.Component;
import static no.nav.melosys.eessi.config.MDCOperations.*;

@Component
@Profile("!local-q2")
public class SedMottattConsumer extends AbstractConsumerSeekAware {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SedMottattConsumer.class);
    private final SedMottakService sedMottakService;
    private final SedMetrikker sedMetrikker;
    private final KafkaDLQService kafkaDLQService;

    @KafkaListener(id = "sedMottatt", clientIdPrefix = "melosys-eessi-sedMottatt", topics = "${melosys.kafka.aiven.consumer.mottatt.topic}", containerFactory = "sedMottattHendelseListenerContainerFactory", groupId = "${melosys.kafka.aiven.consumer.mottatt.groupid}", errorHandler = "sedMottattErrorHandler")
    public void sedMottatt(ConsumerRecord<String, SedHendelse> consumerRecord) {
        SedHendelse sedHendelse = consumerRecord.value();
        if (sedHendelse.erIkkeLaBuc()) {
            return;
        }
        putToMDC(SED_ID, sedHendelse.getSedId());
        putToMDC(CORRELATION_ID, UUID.randomUUID().toString());
        log.info("Mottatt melding om sed mottatt: {}, offset: {}", sedHendelse, consumerRecord.offset());
        try {
            sedMottakService.behandleSedMottakHendelse(SedMottattHendelse.builder().sedHendelse(sedHendelse).build());
        } catch (SedAlleredeJournalførtException e) {
            log.warn("SED {} allerede journalført", e.getSedID());
            sedMetrikker.sedMottattAlleredejournalfoert(sedHendelse.getSedType());
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            log.error("Klarte ikke å konsumere melding om sed mottatt: {}\n{}", message, consumerRecord, e);
            sedMetrikker.sedMottattFeilet(sedHendelse.getSedType());
            kafkaDLQService.lagreNySedMottattHendelse(sedHendelse, e.getMessage());
        } finally {
            remove(SED_ID);
            remove(CORRELATION_ID);
        }
    }

    public void settSpesifiktOffsetPåConsumer(long offset) {
        getSeekCallbacks().forEach((tp, callback) -> callback.seek(tp.topic(), tp.partition(), offset));
    }

    @java.lang.SuppressWarnings("all")
    public SedMottattConsumer(final SedMottakService sedMottakService, final SedMetrikker sedMetrikker, final KafkaDLQService kafkaDLQService) {
        this.sedMottakService = sedMottakService;
        this.sedMetrikker = sedMetrikker;
        this.kafkaDLQService = kafkaDLQService;
    }
}
