package no.nav.melosys.eessi;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostRequest;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.kafkadlq.KafkaDLQ;
import no.nav.melosys.eessi.models.kafkadlq.SedSendtHendelseKafkaDLQ;
import no.nav.melosys.eessi.repository.FagsakRinasakKoblingRepository;
import no.nav.melosys.eessi.repository.KafkaDLQRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Slf4j
class SedSendtTestIT extends ComponentTestBase {

    @Autowired
    private FagsakRinasakKoblingRepository fagsakRinasakKoblingRepository;

    @Autowired
    private KafkaDLQRepository kafkaDLQRepository;

    final String rinaSaksnummer = Integer.toString(new Random().nextInt(100000));
    final long arkivsakID = 11111119;

    @Captor
    ArgumentCaptor<OpprettJournalpostRequest> argumentCaptor;

    @Test
    void sedSendt_saksrelasjonFinnes_journalpostOpprettesOgFerdigstilles() throws Exception {
        mockPerson();
        mockArkivsak();
        lagFagsakRinasakKobling();

        kafkaTestConsumer.reset(1);
        kafkaTemplate.send(lagSedSendtRecord(mockData.sedHendelse(rinaSaksnummer, UUID.randomUUID().toString(), FNR))).get();
        kafkaTestConsumer.doWait(1_500L);

        verify(journalpostapiConsumer, timeout(15000L)).opprettJournalpost(argumentCaptor.capture(), eq(true));

        assertThat(argumentCaptor.getValue()).extracting(OpprettJournalpostRequest::getSak)
            .extracting(OpprettJournalpostRequest.Sak::getArkivsaksnummer)
            .isEqualTo(Long.toString(arkivsakID));
    }

    @Test
    void sedSendt_feilerVedOpprettelseAvJournalpostFørsteGang_lagrerIDLQ() throws Exception {
        mockPerson();
        mockArkivsak();
        lagFagsakRinasakKobling();
        String sedId = UUID.randomUUID().toString();

        when(journalpostapiConsumer.opprettJournalpost(any(OpprettJournalpostRequest.class), anyBoolean()))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY))
            .thenAnswer(a -> mockData.journalpostResponse(a.getArgument(1, Boolean.class)));

        kafkaTestConsumer.reset(1);
        kafkaTemplate.send(lagSedSendtRecord(mockData.sedHendelse(rinaSaksnummer, sedId, FNR))).get();
        kafkaTestConsumer.doWait(5_000L);

        verify(journalpostapiConsumer, timeout(10_000L).times(1)).opprettJournalpost(argumentCaptor.capture(), eq(true));

        assertThat(argumentCaptor.getValue()).extracting(OpprettJournalpostRequest::getSak)
            .extracting(OpprettJournalpostRequest.Sak::getArkivsaksnummer)
            .isEqualTo(Long.toString(arkivsakID));

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        CompletableFuture<List<KafkaDLQ>> future = new CompletableFuture<>();

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            List<KafkaDLQ> dlqList = kafkaDLQRepository.findAll();
            boolean hasValidElement = dlqList.stream()
                .anyMatch(dlq -> dlq instanceof SedSendtHendelseKafkaDLQ &&
                    sedId.equals(((SedSendtHendelseKafkaDLQ) dlq).getSedSendtHendelse().getSedId()));

            assertThat(hasValidElement)
                .overridingErrorMessage("Forventet å finne feilet Kafka-melding i databasen")
                .isTrue();
        });
    }

    private void lagFagsakRinasakKobling() {
        var fagsakRinasakKobling = new FagsakRinasakKobling();
        fagsakRinasakKobling.setRinaSaksnummer(rinaSaksnummer);
        fagsakRinasakKobling.setGsakSaksnummer(arkivsakID);
        fagsakRinasakKobling.setBucType(BucType.LA_BUC_04);
        fagsakRinasakKoblingRepository.save(fagsakRinasakKobling);
    }

    private void mockArkivsak() {
        String arkivsakIdString = Long.toString(arkivsakID);
        when(sakConsumer.getSak(arkivsakIdString)).thenReturn(
            Sak.builder()
                .id(arkivsakIdString)
                .tema("MED")
                .aktoerId(AKTOER_ID)
                .build()
        );
    }

    protected ProducerRecord<String, Object> lagSedSendtRecord(SedHendelse sedHendelse) {
        return new ProducerRecord<>(EESSIBASIS_SEDSENDT_V_1, "key", sedHendelse);
    }

}
