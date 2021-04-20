package no.nav.melosys.eessi;

import java.util.Collections;
import java.util.Random;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import no.finn.unleash.FakeUnleash;
import no.nav.melosys.eessi.integration.oppgave.OppgaveEndretDto;
import no.nav.melosys.eessi.integration.oppgave.OppgaveMetadataKey;
import no.nav.melosys.eessi.integration.oppgave.OpprettOppgaveResponseDto;
import no.nav.melosys.eessi.integration.pdl.dto.PDLIdent;
import no.nav.melosys.eessi.integration.pdl.dto.PDLSokHit;
import no.nav.melosys.eessi.integration.pdl.dto.PDLSokPerson;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.melosys.eessi.integration.pdl.dto.PDLIdentGruppe.FOLKEREGISTERIDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
class SedMottakTestIT extends ComponentTestBase {

    @Autowired
    BucIdentifiseringOppgRepository bucIdentifiseringOppgRepository;
    @Autowired
    SaksrelasjonService saksrelasjonService;


    final String rinaSaksnummer = Integer.toString(new Random().nextInt(100000));

    @BeforeEach
    void initierFeaturetoggle() {
        ((FakeUnleash) unleash).enable("melosys.eessi.en_identifisering_oppg");
    }

    @Test
    void sedMottattMedFnr_blirIdentifisert_publiseresKafka() throws Exception {
        final var sedID = UUID.randomUUID().toString();
        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(mockData.sed(FØDSELSDATO, STATSBORGERSKAP, FNR));

        mockPerson(FNR, AKTOER_ID);

        // Venter på to Kafka-meldinger: den vi selv legger på topic som input, og den som kommer som output
        kafkaTestConsumer.reset(2);
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID, FNR))).get();
        kafkaTestConsumer.doWait(5_000L);

        assertThat(hentRecords()).hasSize(1)
                .extracting(Object::toString)
                .singleElement()
                .matches(e -> e.contains("2019-06-01") && e.contains("2019-12-01"));
    }

    @Test
    void sedMottattUtenFnr_søkIdentifiserer_sendtPåTopic() throws Exception {
        final var sedID = UUID.randomUUID().toString();
        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(mockData.sed(FØDSELSDATO, STATSBORGERSKAP, null));

        var pdlSøkPerson = new PDLSokPerson();
        var søkHits = new PDLSokHit();
        søkHits.setIdenter(Collections.singleton(new PDLIdent(FOLKEREGISTERIDENT, FNR)));
        pdlSøkPerson.setHits(Collections.singleton(søkHits));
        when(pdlConsumer.søkPerson(any())).thenReturn(pdlSøkPerson);
        mockPerson(FNR, AKTOER_ID);

        kafkaTestConsumer.reset(2);
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID, null))).get();
        kafkaTestConsumer.doWait(5_000L);

        assertThat(hentRecords()).hasSize(1);
    }

    @Test
    void toSedMottattUtenFnr_oppretterIdentifiseringsoppgave_reagererPåEndretOppgave() throws Exception {
        final var sedID = UUID.randomUUID().toString();
        final var sedID2 = UUID.randomUUID().toString();
        final var oppgaveID = Integer.toString(new Random().nextInt(100000));

        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(mockData.sed(FØDSELSDATO, STATSBORGERSKAP, null));
        when(pdlConsumer.søkPerson(any())).thenReturn(new PDLSokPerson());
        when(oppgaveConsumer.opprettOppgave(any())).thenReturn(new OpprettOppgaveResponseDto(oppgaveID));

        kafkaTestConsumer.reset(2);
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID, null))).get();
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID2, null))).get();
        kafkaTestConsumer.doWait(5_000L);

        verify(oppgaveConsumer, timeout(6000)).opprettOppgave(any());
        assertThat(hentRecords()).isEmpty();
        assertThat(bucIdentifiseringOppgRepository.findByOppgaveId(oppgaveID)).isPresent();

        kafkaTestConsumer.reset(4);
        kafkaTemplate.send(lagOppgaveIdentifisertRecord(oppgaveID, sedID)).get();
        kafkaTemplate.send(lagOppgaveIdentifisertRecord(oppgaveID, sedID)).get();
        kafkaTestConsumer.doWait(5_000L);

        assertThat(hentRecords()).hasSize(2);
    }

    @Test
    void sedMottattIkkeIdentifisert_saksrelasjonBlirOppdatert_publisererPåKafka() throws Exception {
        final var sedID = UUID.randomUUID().toString();
        final var oppgaveID = Integer.toString(new Random().nextInt(100000));

        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(mockData.sed(FØDSELSDATO, STATSBORGERSKAP, null));
        when(pdlConsumer.søkPerson(any())).thenReturn(new PDLSokPerson());
        when(oppgaveConsumer.opprettOppgave(any())).thenReturn(new OpprettOppgaveResponseDto(oppgaveID));

        kafkaTestConsumer.reset(1);
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID, null))).get();
        kafkaTestConsumer.doWait(5_000L);

        verify(oppgaveConsumer, timeout(6_000L)).opprettOppgave(any());

        final var arkivsakID = 123L;
        final var sak = new Sak();
        sak.setAktoerId(AKTOER_ID);
        when(sakConsumer.getSak(Long.toString(arkivsakID))).thenReturn(sak);

        kafkaTestConsumer.reset(1);
        saksrelasjonService.lagreKobling(arkivsakID, rinaSaksnummer, BucType.LA_BUC_04);
        kafkaTestConsumer.doWait(5_000L);

        assertThat(hentRecords()).hasSize(1);

    }

    private ProducerRecord<String, Object> lagOppgaveIdentifisertRecord(String oppgaveID, String aktørID) {
        return new ProducerRecord<>("oppgave-endret", "key", lagOppgaveEndret(oppgaveID, aktørID));
    }

    private OppgaveEndretDto lagOppgaveEndret(String oppgaveID, String aktørID) {
        var oppgaveEndret = new OppgaveEndretDto();
        oppgaveEndret.setId(Long.valueOf(oppgaveID));
        oppgaveEndret.setAktørId(aktørID);
        oppgaveEndret.setOppgavetype("JFR");
        oppgaveEndret.setStatuskategori("AAPEN");
        oppgaveEndret.setTema("MED");
        oppgaveEndret.setTildeltEnhetsnr("4530");
        oppgaveEndret.getMetadata().put(OppgaveMetadataKey.RINA_SAKID, "123");
        return oppgaveEndret;
    }

}
