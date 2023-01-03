package no.nav.melosys.eessi.kafka.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.journalpostapi.SedAlleredeJournalførtException;
import no.nav.melosys.eessi.metrikker.SedMetrikker;
import no.nav.melosys.eessi.service.joark.OpprettUtgaaendeJournalpostService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static no.nav.melosys.eessi.config.MDCOperations.*;


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
        topics = "${melosys.kafka.aiven.consumer.sendt.topic}",
        containerFactory = "sedHendelseListenerContainerFactory",
        groupId = "${melosys.kafka.aiven.consumer.sendt.groupid}"
    )
    public void sedSendt(ConsumerRecord<String, SedHendelse> consumerRecord) {
        SedHendelse sedSendt = consumerRecord.value();
        putToMDC(SED_ID, sedSendt.getSedId());
        log.info("Mottatt melding om sed sendt: {}, offset: {}", sedSendt, consumerRecord.offset());

        try {
            String journalpostId = opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt);
            log.info("Journalpost opprettet med id: {}", journalpostId);
            sedMetrikker.sedSendt(sedSendt.getSedType());
        } catch (SedAlleredeJournalførtException e) {
            log.info("SED {} allerede journalført", e.getSedID());
        } finally {
            remove(SED_ID);
        }
    }
}
