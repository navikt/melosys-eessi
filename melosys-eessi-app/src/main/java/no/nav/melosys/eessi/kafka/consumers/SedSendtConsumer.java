// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.kafka.consumers;

import java.util.UUID;

import no.nav.melosys.eessi.service.journalfoering.OpprettUtgaaendeJournalpostService;
import no.nav.melosys.eessi.service.kafkadlq.KafkaDLQService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static no.nav.melosys.eessi.config.MDCOperations.*;

@Service
@Profile("!local-q2")
public class SedSendtConsumer {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SedSendtConsumer.class);
    private final OpprettUtgaaendeJournalpostService opprettUtgaaendeJournalpostService;
    private final KafkaDLQService kafkaDLQService;

    @KafkaListener(clientIdPrefix = "melosys-eessi-sedSendt", topics = "${melosys.kafka.aiven.consumer.sendt.topic}", containerFactory = "sedSendtHendelseListenerContainerFactory", groupId = "${melosys.kafka.aiven.consumer.sendt.groupid}")
    public void sedSendt(ConsumerRecord<String, SedHendelse> consumerRecord) {
        SedHendelse sedSendtHendelse = consumerRecord.value();
        putToMDC(SED_ID, sedSendtHendelse.getSedId());
        putToMDC(CORRELATION_ID, UUID.randomUUID().toString());
        log.info("Mottatt melding om sed sendt: {}, offset: {}", sedSendtHendelse, consumerRecord.offset());
        try {
            opprettUtgaaendeJournalpostService.behandleSedSendtHendelse(sedSendtHendelse);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            log.error("Klarte ikke å konsumere melding om sed sendt: {}\n{}", message, consumerRecord, e);
            kafkaDLQService.lagreSedSendtHendelse(sedSendtHendelse, e.getMessage());
        } finally {
            remove(SED_ID);
            remove(CORRELATION_ID);
        }
    }

    @java.lang.SuppressWarnings("all")
    public SedSendtConsumer(final OpprettUtgaaendeJournalpostService opprettUtgaaendeJournalpostService, final KafkaDLQService kafkaDLQService, final SaksrelasjonService saksrelasjonService) {
        this.opprettUtgaaendeJournalpostService = opprettUtgaaendeJournalpostService;
        this.kafkaDLQService = kafkaDLQService;
    }
}
