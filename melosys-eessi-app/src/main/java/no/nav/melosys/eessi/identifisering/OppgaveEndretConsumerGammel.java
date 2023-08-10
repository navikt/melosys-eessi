package no.nav.melosys.eessi.identifisering;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.finn.unleash.Unleash;
import no.nav.melosys.eessi.service.kafkadlq.KafkaDLQService;
import no.nav.melosys.eessi.service.oppgave.OppgaveEndretServiceGammel;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AbstractConsumerSeekAware;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.config.MDCOperations.*;

@Slf4j
@Component
@AllArgsConstructor
public class OppgaveEndretConsumerGammel extends AbstractConsumerSeekAware {

    private final OppgaveEndretServiceGammel oppgaveEndretServiceGammel;
    private final KafkaDLQService kafkaDLQService;
    private final Unleash unleash;

    @KafkaListener(
        id = "oppgaveEndret",
        clientIdPrefix = "melosys-eessi-oppgaveEndret",
        topics = "${melosys.kafka.consumer.oppgave-endret.topic}",
        containerFactory = "oppgaveListenerContainerFactory",
        groupId = "${melosys.kafka.consumer.oppgave-endret.groupid}")
    public void oppgaveEndret(ConsumerRecord<String, OppgaveEndretHendelseGammel> consumerRecord) {
        if (!unleash.isEnabled("melosys.eessi.oppgavehandtering_oppgavehendelser_aiven")) {
            final var oppgaveEndretHendelse = consumerRecord.value();
            putToMDC(CORRELATION_ID, UUID.randomUUID().toString());
            log.info("Mottatt melding om oppgave endret: {}", oppgaveEndretHendelse.getId());

            try {
                oppgaveEndretServiceGammel.behandleOppgaveEndretHendelse(oppgaveEndretHendelse);
            } catch (Exception e) {
                String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                log.error("Klarte ikke å konsumere melding om oppgave endret: {}\n{}", message, consumerRecord, e);

                kafkaDLQService.lagreOppgaveEndretHendelseGammel(oppgaveEndretHendelse, e.getMessage());
            } finally {
                remove(CORRELATION_ID);
            }
        }
        if (unleash.isEnabled("melosys.eessi.oppgavehandtering_oppgavehendelser_aiven_logg")) {
            log.info("Toggle for oppgavehendelse er slått på, konsumerer ikke mottatt melding om oppgave endret: {}", consumerRecord.value().getId());
        }
    }

    public void settSpesifiktOffsetPåConsumer(long offset) {
        getSeekCallbacks().forEach((tp, callback) -> callback.seek(tp.topic(), tp.partition(), offset));
    }
}
