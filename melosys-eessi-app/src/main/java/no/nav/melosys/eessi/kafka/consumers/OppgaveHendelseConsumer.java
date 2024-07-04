// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.kafka.consumers;

import java.util.UUID;
import no.nav.melosys.eessi.identifisering.OppgaveKafkaAivenRecord;
import no.nav.melosys.eessi.service.kafkadlq.KafkaDLQService;
import no.nav.melosys.eessi.service.oppgave.OppgaveEndretService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AbstractConsumerSeekAware;
import org.springframework.stereotype.Component;
import static no.nav.melosys.eessi.config.MDCOperations.*;

@Component
@Profile("!local-q2")
public class OppgaveHendelseConsumer extends AbstractConsumerSeekAware {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OppgaveHendelseConsumer.class);
    private final OppgaveEndretService oppgaveEndretService;
    private final KafkaDLQService kafkaDLQService;

    @KafkaListener(id = "oppgaveHendelse", clientIdPrefix = "melosys-eessi-oppgaveHendelse", topics = "${melosys.kafka.aiven.consumer.oppgave.hendelse.topic}", containerFactory = "oppgaveEndretListenerContainerFactory", groupId = "${melosys.kafka.aiven.consumer.oppgave.hendelse.groupid}", errorHandler = "oppgaveEndretErrorHandler")
    public void oppgaveHendelse(ConsumerRecord<String, OppgaveKafkaAivenRecord> consumerRecord) {
        final var oppgaveEndretHendelse = consumerRecord.value();
        log.info("Mottatt melding om oppgaveHendelse: {}", oppgaveEndretHendelse.oppgave().oppgaveId());
        putToMDC(CORRELATION_ID, UUID.randomUUID().toString());
        try {
            oppgaveEndretService.behandleOppgaveEndretHendelse(oppgaveEndretHendelse);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            log.error("Klarte ikke å konsumere melding om oppgave endret: {}\n{}", message, toString(consumerRecord), e);
            kafkaDLQService.lagreOppgaveEndretHendelse(oppgaveEndretHendelse, e.getMessage());
        } finally {
            remove(CORRELATION_ID);
        }
    }

    private static String toString(ConsumerRecord<String, OppgaveKafkaAivenRecord> consumerRecord) {
        // fjern ident for å anonymisere
        return consumerRecord.toString().replaceAll("\\bident=\\d+", "ident=xxxxxxxxxxx");
    }

    public void settSpesifiktOffsetPåConsumer(long offset) {
        getSeekCallbacks().forEach((tp, callback) -> callback.seek(tp.topic(), tp.partition(), offset));
    }

    @java.lang.SuppressWarnings("all")
    public OppgaveHendelseConsumer(final OppgaveEndretService oppgaveEndretService, final KafkaDLQService kafkaDLQService) {
        this.oppgaveEndretService = oppgaveEndretService;
        this.kafkaDLQService = kafkaDLQService;
    }
}
