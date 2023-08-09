package no.nav.melosys.eessi.kafka.consumers;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.OppgaveKafkaAivenRecord;
import no.nav.melosys.eessi.service.kafkadlq.KafkaDLQService;
import no.nav.melosys.eessi.service.oppgave.OppgaveEndretService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AbstractConsumerSeekAware;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.config.MDCOperations.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class OppgaveEndretConsumer extends AbstractConsumerSeekAware {

    private final OppgaveEndretService oppgaveEndretService;
    private final KafkaDLQService kafkaDLQService;

    @KafkaListener(
        id = "oppgaveEndret",
        clientIdPrefix = "melosys-eessi-oppgaveEndret",
        topics = "${melosys.kafka.aiven.consumer.oppgave.endret.topic}",
        containerFactory = "oppgaveEndretListenerContainerFactory",
        groupId = "${melosys.kafka.aiven.consumer.oppgave.endret.groupid}",
        errorHandler = "oppgaveEndretErrorHandler"
    )
    public void oppgaveEndret(ConsumerRecord<String, OppgaveKafkaAivenRecord> consumerRecord) {
        final var oppgaveEndretHendelse = consumerRecord.value();
        log.info("Mottatt melding om oppgave endret: {}", oppgaveEndretHendelse);

        putToMDC(CORRELATION_ID, UUID.randomUUID().toString());

        try {
            oppgaveEndretService.behandleOppgaveEndretHendelse(oppgaveEndretHendelse);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            log.error("Klarte ikke å konsumere melding om oppgave endret: {}\n{}", message, consumerRecord, e);

            kafkaDLQService.lagreOppgaveEndretHendelse(oppgaveEndretHendelse, e.getMessage());
        } finally {
            remove(CORRELATION_ID);
        }
    }

    public void settSpesifiktOffsetPåConsumer(long offset) {
        getSeekCallbacks().forEach((tp, callback) -> callback.seek(tp.topic(), tp.partition(), offset));
    }
}
