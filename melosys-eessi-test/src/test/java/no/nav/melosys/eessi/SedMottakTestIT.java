package no.nav.melosys.eessi;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.finn.unleash.FakeUnleash;
import no.nav.melosys.eessi.integration.oppgave.HentOppgaveDto;
import no.nav.melosys.eessi.integration.pdl.dto.PDLIdent;
import no.nav.melosys.eessi.integration.pdl.dto.PDLSokHit;
import no.nav.melosys.eessi.integration.pdl.dto.PDLSokPerson;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.melosys.eessi.integration.pdl.dto.PDLIdentGruppe.FOLKEREGISTERIDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
class SedMottakTestIT extends ComponentTestBase {

    @Autowired
    BucIdentifiseringOppgRepository bucIdentifiseringOppgRepository;
    @Autowired
    SedMottattHendelseRepository sedMottattHendelseRepository;

    final String rinaSaksnummer = Integer.toString(new Random().nextInt(100000));

    @BeforeEach
    void initierFeaturetoggle() {
        ((FakeUnleash) unleash).enable("melosys.eessi.en_identifisering_oppg");
    }

    @Test
    void sedX100Mottat_return() throws Exception {
        final var sedID = UUID.randomUUID().toString();
        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(mockData.x100Sed());

        mockPerson(FNR, AKTOER_ID);

        // Venter på en Kafka-melding: den vi selv legger på topic som input, skal ikke gjøre noe med X100
        kafkaTestConsumer.reset(1);
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(Integer.toString(new Random().nextInt(100000)), sedID, FNR))).get();
        kafkaTestConsumer.doWait(5_000L);

        assertThat(hentMelosysEessiRecords()).isEmpty();
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

        assertMelosysEessiMelding(hentMelosysEessiRecords(), 1);
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

        assertMelosysEessiMelding(hentMelosysEessiRecords(), 1);
    }

    @Test
    void toSedMottattUtenFnr_oppretterIdentifiseringsoppgave_reagererPåEndretOppgave() throws Exception {
        final var sedID = UUID.randomUUID().toString();
        final var sedID2 = UUID.randomUUID().toString();
        final var oppgaveID = Integer.toString(new Random().nextInt(100000));
        final var oppgaveDto = new HentOppgaveDto(oppgaveID, "AAPEN");
        oppgaveDto.setStatus("OPPRETTET");

        mockPerson(FNR, AKTOER_ID);

        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(mockData.sed(FØDSELSDATO, STATSBORGERSKAP, null));
        when(pdlConsumer.søkPerson(any())).thenReturn(new PDLSokPerson());
        when(oppgaveConsumer.opprettOppgave(any())).thenReturn(oppgaveDto);
        when(oppgaveConsumer.hentOppgave(oppgaveID)).thenReturn(oppgaveDto);

        kafkaTestConsumer.reset(2);
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID, null))).get();
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID2, null))).get();
        kafkaTestConsumer.doWait(5_000L);

        await().atMost(Duration.ofSeconds(4))
            .pollInterval(Duration.ofSeconds(1))
            .until(() -> sedMottattHendelseRepository.countAllByRinaSaksnummer(rinaSaksnummer) == 2);

        verify(oppgaveConsumer, timeout(6000)).opprettOppgave(any());
        assertThat(hentMelosysEessiRecords()).isEmpty();
        assertThat(bucIdentifiseringOppgRepository.findByOppgaveId(oppgaveID)).isPresent();

        kafkaTestConsumer.reset(3);
        kafkaTemplate.send(lagOppgaveIdentifisertRecord(oppgaveID)).get();
        kafkaTestConsumer.doWait(5_000L);

        verify(oppgaveConsumer, timeout(4000)).oppdaterOppgave(eq(oppgaveID), any());

        assertMelosysEessiMelding(hentMelosysEessiRecords(), 2);
    }

    @Test
    void toSedMottattIdentifisert_publisererPåKafka() throws Exception {
        final var sedID = UUID.randomUUID().toString();
        final var sedID2 = UUID.randomUUID().toString();
        final var oppgaveID = Integer.toString(new Random().nextInt(100000));
        final var oppgaveDto = new HentOppgaveDto(oppgaveID, "AAPEN");
        oppgaveDto.setStatus("OPPRETTET");

        mockPerson(FNR, AKTOER_ID);

        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(mockData.sed(FØDSELSDATO, STATSBORGERSKAP, FNR));
        when(pdlConsumer.søkPerson(any())).thenReturn(new PDLSokPerson());

        kafkaTestConsumer.reset(2);
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID, FNR))).get();
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID2, FNR))).get();
        kafkaTestConsumer.doWait(5_000L);

        await().atMost(Duration.ofSeconds(4))
            .pollInterval(Duration.ofSeconds(1))
            .until(() -> sedMottattHendelseRepository.countAllByRinaSaksnummer(rinaSaksnummer) == 2);

        assertMelosysEessiMelding(hentMelosysEessiRecords(), 2);
    }

    @Test
    void sedMottattIkkeIdentifisert_oppgaveBlirIdentifisertOgMarkertSomFeilIdentifisert_flyttesTilIdOgFordeling() throws Exception {
        final var sedID = UUID.randomUUID().toString();
        final var oppgaveID = Integer.toString(new Random().nextInt(100000));
        final var oppgaveDto = new HentOppgaveDto(oppgaveID, "AAPEN");
        oppgaveDto.setStatus("OPPRETTET");

        mockPerson(FNR, AKTOER_ID, FØDSELSDATO.minusYears(1), "DK");

        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(mockData.sed(FØDSELSDATO, STATSBORGERSKAP, null));
        when(pdlConsumer.søkPerson(any())).thenReturn(new PDLSokPerson());
        when(oppgaveConsumer.opprettOppgave(any())).thenReturn(oppgaveDto);
        when(oppgaveConsumer.hentOppgave(oppgaveID)).thenReturn(oppgaveDto);

        kafkaTestConsumer.reset(1);
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID, null))).get();
        kafkaTestConsumer.doWait(5_000L);

        await().atMost(Duration.ofSeconds(4))
            .pollInterval(Duration.ofSeconds(1))
            .until(() -> sedMottattHendelseRepository.countAllByRinaSaksnummer(rinaSaksnummer) == 1);

        verify(oppgaveConsumer, timeout(6000)).opprettOppgave(any());
        assertThat(hentMelosysEessiRecords()).isEmpty();
        assertThat(bucIdentifiseringOppgRepository.findByOppgaveId(oppgaveID)).isPresent();

        //Forventer kun én melding, som er oppgave-endret record
        kafkaTestConsumer.reset(1);
        kafkaTemplate.send(lagOppgaveIdentifisertRecord(oppgaveID)).get();
        kafkaTestConsumer.doWait(5_000L);

        verify(oppgaveConsumer, timeout(4000)).oppdaterOppgave(eq(oppgaveID), any());

        assertThat(hentMelosysEessiRecords()).isEmpty();
    }

    private ProducerRecord<String, Object> lagOppgaveIdentifisertRecord(String oppgaveID) {
        return new ProducerRecord<>("oppgave-endret", "key", oppgaveEksempel(oppgaveID, FNR, AKTOER_ID));
    }

    @SneakyThrows
    private Object oppgaveEksempel(String oppgaveID, String ident, String aktørID) {
        var path = Paths.get(Objects.requireNonNull(this.getClass().getClassLoader().getResource("oppgave_endret.json")).toURI());
        var oppgaveJsonString = Files.readString(path);
        return new ObjectMapper().readTree(oppgaveJsonString.replaceAll("\\$id", oppgaveID)
            .replaceAll("\\$fnr", ident)
            .replaceAll("\\$aktoerid", aktørID)
            .replaceAll("\\$rinasaksnummer", rinaSaksnummer));
    }
}
