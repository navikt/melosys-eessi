package no.nav.melosys.eessi.kafka.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.metrikker.SedMetrikker;
import no.nav.melosys.eessi.service.joark.OpprettUtgaaendeJournalpostService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class SedSendtConsumer {

    private final OpprettUtgaaendeJournalpostService opprettUtgaaendeJournalpostService;
    private final SedMetrikker sedMetrikker;

    @Autowired
    public SedSendtConsumer(OpprettUtgaaendeJournalpostService opprettUtgaaendeJournalpostService,
            SedMetrikker sedMetrikker) {
        this.opprettUtgaaendeJournalpostService = opprettUtgaaendeJournalpostService;
        this.sedMetrikker = sedMetrikker;
    }

    @KafkaListener(clientIdPrefix = "melosys-eessi-sedSendt",
            topics = "${melosys.kafka.consumer.sendt.topic}", containerFactory = "sedSendtListenerContainerFactory")
    public void sedSendt(ConsumerRecord<String, SedHendelse> consumerRecord) {
        SedHendelse sedSendt = consumerRecord.value();
        log.info("Mottatt melding om sed sendt: {}, offset: {}", sedSendt, consumerRecord.offset());

        try {
            String journalpostId = opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt);
            log.info("Journalpost opprettet med id: {}", journalpostId);
        } catch (Exception e) {
            //todo: legg inn metrikk/alarm
            log.error("Sed ikke journalført: {}, melding: {}", sedSendt, e.getMessage(), e);
        }

        sedMetrikker.sedSendt(sedSendt.getSedType());
    }
}
