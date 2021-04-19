package no.nav.melosys.eessi.kafka.consumers;

import java.util.Collection;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.oppgave.OppgaveDto;
import no.nav.melosys.eessi.integration.oppgave.OppgaveMetadataKey;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import no.nav.melosys.eessi.service.behandling.event.BucIdentifisertEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class OppgaveEndretConsumer {

    private final ApplicationEventPublisher eventPublisher;
    private final BucIdentifiseringOppgRepository bucIdentifiseringOppgRepository;

    private static final Collection<String> GYLDIGE_TEMA = Set.of("MED", "UFM");

    @KafkaListener(clientIdPrefix = "melosys-eessi-oppgaveEndret",
            topics = "${melosys.kafka.consumer.oppgave-endret.topic}", containerFactory = "oppgaveListenerContainerFactory")
    public void oppgaveEndret(ConsumerRecord<String, OppgaveDto> consumerRecord) {
        log.info("Oppgave endret: {}", consumerRecord.value());

        if (erIdentifisertOppgave(consumerRecord.value())) {
            bucIdentifiseringOppgRepository.findByOppgaveId(consumerRecord.value().getId().toString())
                    .ifPresent(b -> {
                        eventPublisher.publishEvent(new BucIdentifisertEvent(b.getRinaSaksnummer(), consumerRecord.value().getAktørId()));
                        //TODO: ferdigstill jfr-oppgave
                    });
        }
    }

    private boolean erIdentifisertOppgave(OppgaveDto oppgaveDto) {
        return "JFR".equals(oppgaveDto.getOppgavetype())
                && "4530".equals(oppgaveDto.getTildeltEnhetsnr())
                && oppgaveDto.getAktørId() != null
                && GYLDIGE_TEMA.contains(oppgaveDto.getTema())
                && "AAPEN".equals(oppgaveDto.getStatuskategori())
                && oppgaveDto.getMetadata().containsKey(OppgaveMetadataKey.RINA_SAKID);
    }
}
