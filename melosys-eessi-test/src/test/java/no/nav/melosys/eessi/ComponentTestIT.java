package no.nav.melosys.eessi;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import no.finn.unleash.FakeUnleash;
import no.nav.melosys.eessi.integration.oppgave.HentOppgaveDto;
import no.nav.melosys.eessi.integration.pdl.dto.PDLIdent;
import no.nav.melosys.eessi.integration.pdl.dto.PDLSokHit;
import no.nav.melosys.eessi.integration.pdl.dto.PDLSokPerson;
import no.nav.melosys.eessi.models.SedMottatt;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static no.nav.melosys.eessi.integration.pdl.dto.PDLIdentGruppe.FOLKEREGISTERIDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
class ComponentTestIT extends ComponentTestBase {

    @BeforeEach
    void initierFeaturetoggle() {
        ((FakeUnleash) unleash).disable("melosys.eessi.en_identifisering_oppg");
    }

    @Test
    @DisplayName("Mottar SED med fnr, person identifisert, sender melding på kafka-topic")
    void sedMottattMedFnr_blirIdentifisert_sendtPåKafkaTopic() throws Exception {
        final var sedID = UUID.randomUUID().toString();
        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(mockData.sed(FØDSELSDATO, STATSBORGERSKAP, FNR));

        mockPerson(FNR, AKTOER_ID);

        // Venter på to Kafka-meldinger: den vi selv legger på topic som input, og den som kommer som output
        kafkaTestConsumer.reset(2);
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(RINA_SAKSNUMMER, sedID, FNR))).get();
        kafkaTestConsumer.doWait(5_000L);

        assertThat(hentRecords()).hasSize(1)
                .extracting(Object::toString)
                .singleElement()
                .matches(e -> e.contains("2019-06-01") && e.contains("2019-12-01"));
    }

    @Test
    @DisplayName("Mottar SED uten fnr, identifiserer etter søk og publiserer på kafka-topic")
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
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(RINA_SAKSNUMMER, sedID, null))).get();
        kafkaTestConsumer.doWait(5_000L);

        assertThat(hentRecords()).hasSize(1);
    }

    @Test
    @DisplayName("Mottar SED uten fnr, ingen resultat fra person-søk, oppretter oppgave til ID og Fordeling")
    void sedMottattUtenFnr_kanIkkeIdentifiserePerson_oppretterOppgave() throws Exception {
        final var sedID = UUID.randomUUID().toString();
        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(mockData.sed(FØDSELSDATO, STATSBORGERSKAP, null));
        when(pdlConsumer.søkPerson(any())).thenReturn(new PDLSokPerson());
        when(oppgaveConsumer.opprettOppgave(any())).thenReturn(new HentOppgaveDto("123", "AAPEN"));

        kafkaTestConsumer.reset(1);
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(RINA_SAKSNUMMER, sedID, null))).get();
        kafkaTestConsumer.doWait(5_000L);

        verify(oppgaveConsumer, timeout(2000)).opprettOppgave(any());
        assertThat(hentRecords()).isEmpty();
    }

    @Test
    @DisplayName("Mottar SED med fnr, person identifisert, teknisk feil ved opprettelse av journalpost, blir lagret")
    void sedMottattMedFnr_tekniskFeilVedOpprettelseAvJournalpost_blirLagret() throws Exception {
        final var sedID = UUID.randomUUID().toString();
        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(mockData.sed(FØDSELSDATO, STATSBORGERSKAP, FNR));

        mockPerson(FNR, AKTOER_ID);
        when(journalpostapiConsumer.opprettJournalpost(any(), anyBoolean())).thenThrow(new IntegrationException("Feil!"));

        kafkaTestConsumer.reset(1);
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(RINA_SAKSNUMMER, sedID, FNR))).get();
        kafkaTestConsumer.doWait(5_000L);

        await().atMost(2, TimeUnit.SECONDS).until(() -> sedMottattRepository.count() > 0);
        assertThat(sedMottattRepository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(
                        s -> s.getSedKontekst().isForsoktIdentifisert(),
                        s -> s.getSedKontekst().getNavIdent(),
                        s -> s.getSedKontekst().journalpostOpprettet(),
                        SedMottatt::getFeiledeForsok,
                        SedMottatt::isFeilet,
                        SedMottatt::isFerdig
                ).containsExactly(
                true,
                FNR,
                false,
                1,
                false,
                false
        );
        assertThat(hentRecords()).isEmpty();
    }
}
