package no.nav.melosys.eessi.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.eessi.basis.SedMottatt;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SedMottattConsumer {

    private final OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;

    @Autowired
    public SedMottattConsumer(OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService) {
        this.opprettInngaaendeJournalpostService = opprettInngaaendeJournalpostService;
    }

    @KafkaListener(clientIdPrefix = "melosys-eessi-sedMottatt", topics = "privat-eessi-basis-sedMottatt-v1",
            containerFactory = "sedMottattListenerContainerFactory")
    public void sedMottatt(ConsumerRecord<String, SedMottatt> consumerRecord) {
        SedMottatt sedMottatt = consumerRecord.value();
        log.info("Sed mottatt: {}", sedMottatt);

        try {
            String journalpostId = opprettInngaaendeJournalpostService.arkiverInngaaendeSed(sedMottatt);
            log.info("Midlertidig journalpost opprettet med id {}", journalpostId);
        } catch (IntegrationException e) {
            log.error("Sed ikke journalført: {}, melding: {}", sedMottatt, e.getMessage(), e);
        }
    }
}
