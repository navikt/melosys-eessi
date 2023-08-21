package no.nav.melosys.eessi.kafka.consumers;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.finn.unleash.Unleash;
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
public class OppgaveHendelseConsumer extends AbstractConsumerSeekAware {

    private final OppgaveEndretService oppgaveEndretService;
    private final KafkaDLQService kafkaDLQService;
    private final Unleash unleash;

    @KafkaListener(
        id = "oppgaveHendelse",
        clientIdPrefix = "melosys-eessi-oppgaveHendelse",
        topics = "${melosys.kafka.aiven.consumer.oppgave.hendelse.topic}",
        containerFactory = "oppgaveEndretListenerContainerFactory",
        groupId = "${melosys.kafka.aiven.consumer.oppgave.hendelse.groupid}",
        errorHandler = "oppgaveEndretErrorHandler"
    )
    public void oppgaveHendelse(ConsumerRecord<String, OppgaveKafkaAivenRecord> consumerRecord) {
        if (unleash.isEnabled("melosys.eessi.oppgavehandtering_oppgavehendelser_aiven")) {
            final var oppgaveEndretHendelse = consumerRecord.value();
            if (unleash.isEnabled("melosys.eessi.oppgavehandtering_oppgavehendelser_aiven_logg")) {
                log.info("Mottatt melding om oppgaveHendelse: {}", oppgaveEndretHendelse.oppgave().oppgaveId());
            }
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
        } else if (unleash.isEnabled("melosys.eessi.oppgavehandtering_oppgavehendelser_aiven_logg")) {
            log.info("Toggle for oppgavehendelse er slått av, konsumerer ikke mottatt melding om oppgavehendelse: {}", consumerRecord.value().oppgave().oppgaveId());
        }
    }

    public void settSpesifiktOffsetPåConsumer(long offset) {
        getSeekCallbacks().forEach((tp, callback) -> callback.seek(tp.topic(), tp.partition(), offset));
    }
}
