package no.nav.melosys.eessi.identifisering;

import java.util.Collection;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.event.BucIdentifisertEvent;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
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
    private final OppgaveService oppgaveService;

    private static final Collection<String> GYLDIGE_TEMA = Set.of("MED", "UFM");

    @KafkaListener(
            clientIdPrefix = "melosys-eessi-oppgaveEndret",
            topics = "${melosys.kafka.consumer.oppgave-endret.topic}",
            containerFactory = "oppgaveListenerContainerFactory")
    public void oppgaveEndret(ConsumerRecord<String, OppgaveEndretHendelse> consumerRecord) {
        final var oppgave = consumerRecord.value();
        log.debug("Oppgave endret: {}", oppgave);

        if (erIdentifisertOppgave(oppgave)) {
            log.info("Oppgave {} markert som identifisert. Søker etter tilknyttet RINA-sak", oppgave.getId());
            bucIdentifiseringOppgRepository.findByOppgaveId(oppgave.getId().toString())
                    .ifPresent(b -> {
                        log.info("BUC {} identifisert av oppgave {}", b.getRinaSaksnummer(), b.getOppgaveId());
                        eventPublisher.publishEvent(new BucIdentifisertEvent(b.getRinaSaksnummer(), oppgave.hentAktørID()));
                        oppgaveService.ferdigstillOppgave(b.getOppgaveId(), oppgave.getVersjon());
                    });
        }
    }

    private boolean erIdentifisertOppgave(OppgaveEndretHendelse oppgaveEndretHendelse) {
        return "JFR".equals(oppgaveEndretHendelse.getOppgavetype())
                && "4530".equals(oppgaveEndretHendelse.getTildeltEnhetsnr())
                && oppgaveEndretHendelse.harAktørID()
                && GYLDIGE_TEMA.contains(oppgaveEndretHendelse.getTema())
                && oppgaveEndretHendelse.erÅpen()
                && oppgaveEndretHendelse.harMetadataRinasaksnummer();
    }
}
