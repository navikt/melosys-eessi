package no.nav.melosys.eessi.identifisering;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.service.kafkadlq.KafkaDLQService;
import no.nav.melosys.eessi.service.oppgave.OppgaveEndretService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AbstractConsumerSeekAware;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.config.MDCOperations.*;

@Slf4j
@Component
@AllArgsConstructor
public class OppgaveEndretConsumer extends AbstractConsumerSeekAware {

    private final OppgaveEndretService oppgaveEndretService;
    private final KafkaDLQService kafkaDLQService;

    @KafkaListener(
        id = "oppgaveEndret",
        clientIdPrefix = "melosys-eessi-oppgaveEndret",
        topics = "${melosys.kafka.consumer.oppgave-endret.topic}",
        containerFactory = "oppgaveListenerContainerFactory",
        groupId = "${melosys.kafka.consumer.oppgave-endret.groupid}")
    public void oppgaveEndret(ConsumerRecord<String, OppgaveEndretHendelse> consumerRecord) {
        final var oppgaveEndretHendelse = consumerRecord.value();

        putToMDC(CORRELATION_ID, UUID.randomUUID().toString());

        log.debug("Mottatt melding om oppgave endret: {}", oppgaveEndretHendelse);

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
