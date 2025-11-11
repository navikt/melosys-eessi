package no.nav.melosys.eessi;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostRequest;
import no.nav.melosys.eessi.integration.pdl.dto.PDLSokHit;
import no.nav.melosys.eessi.integration.pdl.dto.PDLSokPerson;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.BucIdentifisert;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.kafkadlq.KafkaDLQ;
import no.nav.melosys.eessi.models.kafkadlq.SedSendtHendelseKafkaDLQ;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.repository.BucIdentifisertRepository;
import no.nav.melosys.eessi.repository.FagsakRinasakKoblingRepository;
import no.nav.melosys.eessi.repository.KafkaDLQRepository;
import no.nav.melosys.eessi.repository.SedSendtHendelseRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static no.nav.melosys.eessi.models.BucType.H_BUC_01;
import static no.nav.melosys.eessi.models.BucType.H_BUC_05;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Slf4j
class SedSendtTestIT extends ComponentTestBase {

    @Autowired
    private FagsakRinasakKoblingRepository fagsakRinasakKoblingRepository;

    @Autowired
    private SedSendtHendelseRepository sedSendtHendelseRepository;

    @Autowired
    private KafkaDLQRepository kafkaDLQRepository;

    @MockitoBean
    private PersonFasade personFasade;

    @Autowired
    private BucIdentifisertRepository bucIdentifisertRepository;

    final String rinaSaksnummer = Integer.toString(new Random().nextInt(100000));
    final long arkivsakID = 11111119;

    @Captor
    ArgumentCaptor<OpprettJournalpostRequest> argumentCaptor;

    @Test
    void sedSendt_saksrelasjonFinnes_journalpostOpprettesOgFerdigstilles() throws Exception {
        mockPerson();
        mockArkivsak();
        mockIdentifisertPerson();
        lagFagsakRinasakKobling();

        kafkaTestConsumer.reset(1);
        kafkaTemplate.send(lagSedSendtRecord(mockData.sedHendelse(rinaSaksnummer, UUID.randomUUID().toString(), FNR))).get();
        kafkaTestConsumer.doWait(1_500L);
        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(new SED());

        verify(journalpostapiConsumer, timeout(15000L)).opprettJournalpost(argumentCaptor.capture(), eq(true));

        assertThat(argumentCaptor.getValue()).extracting(OpprettJournalpostRequest::getSak)
            .extracting(OpprettJournalpostRequest.Sak::getArkivsaksnummer)
            .isEqualTo(Long.toString(arkivsakID));
    }

    @Test
    void sedSendt_saksrelasjonFinnesPåHBuc_journalpostOpprettesOgFerdigstilles() throws Exception {
        mockPerson();
        mockArkivsak();
        mockIdentifisertPerson();
        lagFagsakRinasakKobling();

        kafkaTestConsumer.reset(1);
        SedHendelse sedHendelse = mockData.sedHendelse(rinaSaksnummer, UUID.randomUUID().toString(), FNR);
        sedHendelse.setSektorKode("H");
        sedHendelse.setBucType(H_BUC_01.name());
        kafkaTemplate.send(lagSedSendtRecord(sedHendelse)).get();
        kafkaTestConsumer.doWait(1_500L);
        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(new SED());

        verify(journalpostapiConsumer, timeout(15000L)).opprettJournalpost(argumentCaptor.capture(), eq(true));

        assertThat(argumentCaptor.getValue()).extracting(OpprettJournalpostRequest::getSak)
            .extracting(OpprettJournalpostRequest.Sak::getArkivsaksnummer)
            .isEqualTo(Long.toString(arkivsakID));
    }

    @Test
    void sedSendt_HBucErIkkeFraMelosys_meldingIgnoreres() throws Exception {
        mockPerson();
        mockArkivsak();
        mockIdentifisertPerson();

        kafkaTestConsumer.reset(1);
        SedHendelse sedHendelse = mockData.sedHendelse(rinaSaksnummer, UUID.randomUUID().toString(), FNR);
        sedHendelse.setSektorKode("H");
        sedHendelse.setBucType(H_BUC_05.name());
        kafkaTemplate.send(lagSedSendtRecord(sedHendelse)).get();
        kafkaTestConsumer.doWait(1_500L);

        verifyNoInteractions(journalpostapiConsumer);

    }

    @Test
    void sedSendt_tidligereUidentifisertPerson_oppretterFlereJournalPosterRetrospektivt() throws Exception {
        mockPerson();
        mockArkivsak();
        lagFagsakRinasakKobling();

        PDLSokPerson pdlSokPerson = new PDLSokPerson();
        pdlSokPerson.setHits(Collections.singletonList(new PDLSokHit()));
        when(personFasade.soekEtterPerson(any())).thenReturn(List.of());
        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(mockData.sedUkjentPin(LocalDate.of(2000, 1, 1), "DK", null));


        kafkaTestConsumer.reset(3);
        kafkaTemplate.send(lagSedSendtRecord(mockData.sedHendelse(rinaSaksnummer, UUID.randomUUID().toString(), null))).get();
        kafkaTemplate.send(lagSedSendtRecord(mockData.sedHendelse(rinaSaksnummer, UUID.randomUUID().toString(), null))).get();
        await().atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofSeconds(1))
            .until(() -> sedSendtHendelseRepository.findAllByRinaSaksnummerAndAndJournalpostIdIsNull(rinaSaksnummer).size() == 2);

        mockIdentifisertPerson();
        kafkaTemplate.send(lagSedSendtRecord(mockData.sedHendelse(rinaSaksnummer, UUID.randomUUID().toString(), FNR))).get();
        kafkaTestConsumer.doWait(1_500L);
        await().atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofSeconds(1))
            .until(() -> sedSendtHendelseRepository.findAllByRinaSaksnummerAndAndJournalpostIdIsNull(rinaSaksnummer).size() == 0);

        verify(journalpostapiConsumer, times(3)).opprettJournalpost(argumentCaptor.capture(), eq(true));
    }

    @Test
    void sedSendt_uidentifisertPerson_lagresForFremtidigJournalfoering() throws Exception {
        mockArkivsak();
        lagFagsakRinasakKobling();
        PDLSokPerson pdlSokPerson = new PDLSokPerson();
        pdlSokPerson.setHits(Collections.singletonList(new PDLSokHit()));
        when(personFasade.soekEtterPerson(any())).thenReturn(List.of());

        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(mockData.sedUkjentPin(LocalDate.of(2000, 1, 1), "DK", null));

        kafkaTestConsumer.reset(1);
        kafkaTemplate.send(lagSedSendtRecord(mockData.sedHendelse(rinaSaksnummer, UUID.randomUUID().toString(), null))).get();
        kafkaTestConsumer.doWait(3500L);

        await().atMost(Duration.ofSeconds(4))
            .pollInterval(Duration.ofSeconds(1))
            .until(() -> sedSendtHendelseRepository.findAllByRinaSaksnummerAndAndJournalpostIdIsNull(rinaSaksnummer).size() == 1);
        verify(journalpostapiConsumer, never()).opprettJournalpost(any(), anyBoolean());
    }

    @Test
    void sedSendt_feilerVedOpprettelseAvJournalpostFørsteGang_lagrerIDLQ() throws Exception {
        mockPerson();
        mockArkivsak();
        mockIdentifisertPerson();

        lagFagsakRinasakKobling();
        String sedId = UUID.randomUUID().toString();

        when(journalpostapiConsumer.opprettJournalpost(any(OpprettJournalpostRequest.class), anyBoolean()))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY))
            .thenAnswer(a -> mockData.journalpostResponse(a.getArgument(1, Boolean.class)));
        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(new SED());

        kafkaTestConsumer.reset(1);
        kafkaTemplate.send(lagSedSendtRecord(mockData.sedHendelse(rinaSaksnummer, sedId, FNR))).get();
        kafkaTestConsumer.doWait(5_000L);

        verify(journalpostapiConsumer, timeout(10_000L).times(1)).opprettJournalpost(argumentCaptor.capture(), eq(true));

        assertThat(argumentCaptor.getValue()).extracting(OpprettJournalpostRequest::getSak)
            .extracting(OpprettJournalpostRequest.Sak::getArkivsaksnummer)
            .isEqualTo(Long.toString(arkivsakID));

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

    private void mockIdentifisertPerson() {
        BucIdentifisert bucIdentifisert = new BucIdentifisert(0L, rinaSaksnummer, FNR);
        bucIdentifisertRepository.save(bucIdentifisert);
    }

    protected ProducerRecord<String, Object> lagSedSendtRecord(SedHendelse sedHendelse) {
        return new ProducerRecord<>(EESSIBASIS_SEDSENDT_V_1, "key", sedHendelse);
    }
}
