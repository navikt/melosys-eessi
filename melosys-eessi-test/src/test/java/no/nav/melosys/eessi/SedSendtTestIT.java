package no.nav.melosys.eessi;

import java.util.Random;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostRequest;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.repository.FagsakRinasakKoblingRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Slf4j
class SedSendtTestIT extends ComponentTestBase {

    @Autowired
    private FagsakRinasakKoblingRepository fagsakRinasakKoblingRepository;

    final String rinaSaksnummer = Integer.toString(new Random().nextInt(100000));

    @Test
    void sedSendt_saksrelasjonFinnes_journalpostOpprettesOgFerdigstilles() throws Exception {
        final long arkivsakID = 11111119;
        mockPerson(FNR, AKTOER_ID);
        mockArkivsak(arkivsakID);
        lagFagsakRinasakKobling(arkivsakID);

        kafkaTestConsumer.reset(1);
        kafkaTemplate.send(lagSedSendtRecord(mockData.sedHendelse(rinaSaksnummer, UUID.randomUUID().toString(), FNR))).get();
        kafkaTestConsumer.doWait(1500L);

        var captor = ArgumentCaptor.forClass(OpprettJournalpostRequest.class);
        verify(journalpostapiConsumer, timeout(10000L)).opprettJournalpost(captor.capture(), eq(true));

        assertThat(captor.getValue()).extracting(OpprettJournalpostRequest::getSak)
            .extracting(OpprettJournalpostRequest.Sak::getArkivsaksnummer)
            .isEqualTo(Long.toString(arkivsakID));
    }

    @Test
    void sedSendt_feilerVedOpprettelseAvJournalpostFørsteGang_prøverIgjen() throws Exception {
        final long arkivsakID = 11111119;
        mockPerson(FNR, AKTOER_ID);
        mockArkivsak(arkivsakID);
        lagFagsakRinasakKobling(arkivsakID);

        kafkaTestConsumer.reset(1);
        kafkaTemplate.send(lagSedSendtRecord(mockData.sedHendelse(rinaSaksnummer, UUID.randomUUID().toString(), FNR))).get();
        kafkaTestConsumer.doWait(1000L);

        when(journalpostapiConsumer.opprettJournalpost(any(OpprettJournalpostRequest.class), anyBoolean()))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY))
            .thenAnswer(a -> mockData.journalpostResponse(a.getArgument(1, Boolean.class)));

        var captor = ArgumentCaptor.forClass(OpprettJournalpostRequest.class);
        verify(journalpostapiConsumer, timeout(25000L).times(2)).opprettJournalpost(captor.capture(), eq(true));

        assertThat(captor.getValue()).extracting(OpprettJournalpostRequest::getSak)
            .extracting(OpprettJournalpostRequest.Sak::getArkivsaksnummer)
            .isEqualTo(Long.toString(arkivsakID));
    }

    private void lagFagsakRinasakKobling(long arkivsakID) {
        var fagsakRinasakKobling = new FagsakRinasakKobling();
        fagsakRinasakKobling.setRinaSaksnummer(rinaSaksnummer);
        fagsakRinasakKobling.setGsakSaksnummer(arkivsakID);
        fagsakRinasakKobling.setBucType(BucType.LA_BUC_04);
        fagsakRinasakKoblingRepository.save(fagsakRinasakKobling);
    }

    private void mockArkivsak(long arkivsakID) {
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
        return new ProducerRecord<>("eessi-basis-sedSendt-v1", "key", sedHendelse);
    }

}
