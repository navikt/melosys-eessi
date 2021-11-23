package no.nav.melosys.eessi;

import no.finn.unleash.FakeUnleash;
import no.nav.melosys.eessi.integration.oppgave.HentOppgaveDto;
import no.nav.melosys.eessi.integration.pdl.dto.PDLSokPerson;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class OppgaveEndretMottakTestIT extends ComponentTestBase {

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
    void oppgaveEndret_utenKorrektVersjonsnumerFraKafka_ikkeFerdigstill() throws Exception  {
        final var sedID = UUID.randomUUID().toString();
        final var oppgaveID = Integer.toString(new Random().nextInt(100000));
        final var oppgaveDto = new HentOppgaveDto(oppgaveID, "AAPEN", 1);
        oppgaveDto.setStatus("OPPRETTET");

        mockPerson(FNR, AKTOER_ID);

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
        oppgaveDto.setVersjon(2);

        kafkaTestConsumer.reset(1);
        kafkaTemplate.send(lagOppgaveIdentifisertRecord(oppgaveID, FNR ,"1", rinaSaksnummer)).get();
        kafkaTestConsumer.doWait(5_000L);

        verify(oppgaveConsumer, never()).oppdaterOppgave(anyString(),any());
        assertThat(hentMelosysEessiRecords()).isEmpty();
    }

    @Test
    void oppgaveEndret_alleredFerdigStiltOppgave_ikkeFerdigstill() throws Exception  {
        final var sedID = UUID.randomUUID().toString();
        final var oppgaveID = Integer.toString(new Random().nextInt(100000));
        final var oppgaveDto = new HentOppgaveDto(oppgaveID, "AAPEN", 1);
        oppgaveDto.setStatus("OPPRETTET");

        mockPerson(FNR, AKTOER_ID);

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
        oppgaveDto.setVersjon(2);
        oppgaveDto.setStatus("FERDIGSTILT");

        kafkaTestConsumer.reset(1);
        kafkaTemplate.send(lagOppgaveIdentifisertRecord(oppgaveID, FNR ,"2", rinaSaksnummer)).get();
        kafkaTestConsumer.doWait(5_000L);

        //verify(oppgaveConsumer, timeout(4000)).oppdaterOppgave(eq(oppgaveID), any());
        verify(oppgaveConsumer, never()).oppdaterOppgave(anyString(),any());
        assertThat(hentMelosysEessiRecords()).isEmpty();
    }
}
