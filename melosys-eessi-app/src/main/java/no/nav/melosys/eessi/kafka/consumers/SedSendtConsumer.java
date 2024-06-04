package no.nav.melosys.eessi.kafka.consumers;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.service.journalfoering.OpprettUtgaaendeJournalpostService;
import no.nav.melosys.eessi.service.kafkadlq.KafkaDLQService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static no.nav.melosys.eessi.config.MDCOperations.*;
import static no.nav.melosys.eessi.models.BucType.erHBucsomSkalKonsumeres;


@Service
@Slf4j
@RequiredArgsConstructor
@Profile("!local-q2")
public class SedSendtConsumer {

    private final OpprettUtgaaendeJournalpostService opprettUtgaaendeJournalpostService;
    private final KafkaDLQService kafkaDLQService;
    private final SaksrelasjonService saksrelasjonService;

    @KafkaListener(clientIdPrefix = "melosys-eessi-sedSendt",
        topics = "${melosys.kafka.aiven.consumer.sendt.topic}",
        containerFactory = "sedSendtHendelseListenerContainerFactory",
        groupId = "${melosys.kafka.aiven.consumer.sendt.groupid}"
    )
    public void sedSendt(ConsumerRecord<String, SedHendelse> consumerRecord) {
        SedHendelse sedSendtHendelse = consumerRecord.value();
        if(sedSendtHendelse.erIkkeLaBuc() && !erHBucFraMelosys(sedSendtHendelse)){
            return;
        }

        putToMDC(SED_ID, sedSendtHendelse.getSedId());
        putToMDC(CORRELATION_ID, UUID.randomUUID().toString());

        log.info("Mottatt melding om sed sendt: {}, offset: {}", sedSendtHendelse, consumerRecord.offset());
        try {
            opprettUtgaaendeJournalpostService.behandleSedSendtHendelse(sedSendtHendelse);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            log.error("Klarte ikke å konsumere melding om sed sendt: {}\n{}", message, consumerRecord, e);

            kafkaDLQService.lagreSedSendtHendelse(sedSendtHendelse, e.getMessage());
        } finally {
            remove(SED_ID);
            remove(CORRELATION_ID);
        }
    }

    private boolean erHBucFraMelosys(SedHendelse sedSendtHendelse) {
        return erHBucsomSkalKonsumeres(sedSendtHendelse.getBucType()) && erRinaSakIEessi(sedSendtHendelse.getRinaSakId());
    }

    private boolean erRinaSakIEessi(String rinaSakId) {
        return saksrelasjonService.finnVedRinaSaksnummer(rinaSakId).isPresent();
    }
}
