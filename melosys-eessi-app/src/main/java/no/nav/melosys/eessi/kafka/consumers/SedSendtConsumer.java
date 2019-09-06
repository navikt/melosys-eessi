package no.nav.melosys.eessi.kafka.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.metrikker.MetrikkerRegistrering;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.service.joark.OpprettUtgaaendeJournalpostService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class SedSendtConsumer {

    private final OpprettUtgaaendeJournalpostService opprettUtgaaendeJournalpostService;
    private final MetrikkerRegistrering metrikkerRegistrering;

    @Autowired
    public SedSendtConsumer(OpprettUtgaaendeJournalpostService opprettUtgaaendeJournalpostService,
            MetrikkerRegistrering metrikkerRegistrering) {
        this.opprettUtgaaendeJournalpostService = opprettUtgaaendeJournalpostService;
        this.metrikkerRegistrering = metrikkerRegistrering;
    }

    @KafkaListener(clientIdPrefix = "melosys-eessi-sedSendt", topics = "eessi-basis-sedSendt-v1",
            containerFactory = "sedSendtListenerContainerFactory")
    public void sedSendt(ConsumerRecord<String, SedHendelse> consumerRecord) {
        SedHendelse sedSendt = consumerRecord.value();
        log.info("Sed sendt: {}", sedSendt);

        try {
            String journalpostId = opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt);
            log.info("Journalpost opprettet med id: {}", journalpostId);
        } catch (NotFoundException|IntegrationException e) {
            //Settes pga testing for nå, da gsakSaksnummer ikke alltid vil eksistere, ved feks testing direkte fra rina eller lokalt
            log.error("Sed ikke journalført: {}, melding: {}", sedSendt, e.getMessage(), e);
        }
        metrikkerRegistrering.sedSendt(sedSendt);


    }
}