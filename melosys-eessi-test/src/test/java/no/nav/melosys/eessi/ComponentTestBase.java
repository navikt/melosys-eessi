package no.nav.melosys.eessi;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import io.getunleash.FakeUnleash;
import no.nav.melosys.eessi.identifisering.OppgaveKafkaAivenRecord;
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer;
import no.nav.melosys.eessi.integration.journalpostapi.JournalpostapiConsumer;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostRequest;
import no.nav.melosys.eessi.integration.oppgave.OppgaveConsumer;
import no.nav.melosys.eessi.integration.pdl.PDLConsumer;
import no.nav.melosys.eessi.integration.pdl.PdlWebConsumer;
import no.nav.melosys.eessi.integration.saf.SafConsumer;
import no.nav.melosys.eessi.integration.sak.SakConsumer;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.utils.ConsumerRecordPredicates;
import no.nav.melosys.utils.KafkaTestConfig;
import no.nav.melosys.utils.KafkaTestConsumer;
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static no.nav.melosys.eessi.ComponentTestBase.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest(classes = {ComponentTestConfig.class, KafkaTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/kafka-test.properties", properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.test.bean-override.enabled=true"
})
@EmbeddedKafka(controlledShutdown = true, partitions = 1,
    topics = {EESSIBASIS_SEDMOTTATT_V_1, EESSIBASIS_SEDSENDT_V_1, OPPGAVE_ENDRET, TEAMMELOSYS_EESSI_V_1_LOCAL},
    brokerProperties = {"offsets.topic.replication.factor=1", "transaction.state.log.replication.factor=1", "transaction.state.log.min.isr=1"})
@EnableMockOAuth2Server
public abstract class ComponentTestBase extends PostgresTestContainerBase {
    public static final String EESSIBASIS_SEDMOTTATT_V_1 = "eessibasis-sedmottatt-v1";
    public static final String EESSIBASIS_SEDSENDT_V_1 = "eessibasis-sedsendt-v1";
    public static final String OPPGAVE_ENDRET = "oppgavehandtering.oppgavehendelse-v1";
    public static final String TEAMMELOSYS_EESSI_V_1_LOCAL = "teammelosys.eessi.v1-local";

    static final LocalDate FØDSELSDATO = LocalDate.of(2000, 1, 1);
    static final String STATSBORGERSKAP = "NO";
    static final String FNR = "25068420779";
    static final String AKTOER_ID = "1234567890123";
    static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    protected final MockData mockData = new MockData();

    @Autowired
    KafkaTestConsumer kafkaTestConsumer;

    @MockitoBean
    EuxConsumer euxConsumer;

    @MockitoBean
    SakConsumer sakConsumer;

    @MockitoBean
    JournalpostapiConsumer journalpostapiConsumer;

    @MockitoBean
    OppgaveConsumer oppgaveConsumer;

    @MockitoBean
    SafConsumer safConsumer;

    @MockitoBean
    PDLConsumer pdlConsumer;

    @MockitoBean
    PdlWebConsumer pdlWebConsumer;

    @Autowired
    FakeUnleash unleash = new FakeUnleash();

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    protected ProducerRecord<String, Object> lagSedMottattRecord(SedHendelse sedHendelse) {
        return new ProducerRecord<>(EESSIBASIS_SEDMOTTATT_V_1, "key", sedHendelse);
    }


    @BeforeEach
    public void setup() {
        unleash.enableAll();
        when(euxConsumer.hentBUC(anyString())).thenReturn(mockData.buc("rinadokumentid"));
        when(euxConsumer.hentSedMedVedlegg(anyString(), anyString())).thenReturn(mockData.sedMedVedlegg());
        when(journalpostapiConsumer.opprettJournalpost(any(OpprettJournalpostRequest.class), anyBoolean()))
            .thenAnswer(a -> mockData.journalpostResponse(a.getArgument(1, Boolean.class)));
    }

    protected void mockPerson() {
        mockPerson(FØDSELSDATO, STATSBORGERSKAP);
    }

    protected void mockPerson(LocalDate fødselsdato, String statsborgerskap) {
        mockHentPerson(fødselsdato, statsborgerskap);
        mockHentIdenter();
    }

    private void mockHentPerson(LocalDate fødselsdato, String statsborgerskap) {
        var pdlPerson = mockData.pdlPerson(fødselsdato, statsborgerskap);
        when(pdlConsumer.hentPerson(FNR)).thenReturn(pdlPerson);
        when(pdlConsumer.hentPerson(AKTOER_ID)).thenReturn(pdlPerson);
    }

    protected void mockHentIdenter() {
        var pdlIdentListe = mockData.lagPDLIdentListe(FNR, AKTOER_ID);
        when(pdlConsumer.hentIdenter(FNR)).thenReturn(pdlIdentListe);
        when(pdlConsumer.hentIdenter(AKTOER_ID)).thenReturn(pdlIdentListe);
    }

    List<MelosysEessiMelding> hentMelosysEessiRecords() {
        return kafkaTestConsumer.getRecords()
            .stream()
            .filter(ConsumerRecordPredicates.topic(TEAMMELOSYS_EESSI_V_1_LOCAL))
            .map(ConsumerRecord::value)
            .map(this::tilMelosysEessiMelding)
            .collect(Collectors.toList());
    }

    @SneakyThrows
    MelosysEessiMelding tilMelosysEessiMelding(String value) {
        return objectMapper.readValue(value, MelosysEessiMelding.class);
    }

    protected ProducerRecord<String, Object> lagOppgaveIdentifisertRecord(String oppgaveID, String versjon, String rinaSaksnummer) {
        return new ProducerRecord<>(OPPGAVE_ENDRET, "key", oppgaveEksempel(oppgaveID, versjon, rinaSaksnummer));
    }

    @SneakyThrows
    private OppgaveKafkaAivenRecord oppgaveEksempel(String oppgaveID, String versjonsNummer, String rinaSaksnummer) {
        var path = Paths.get(Objects.requireNonNull(this.getClass().getClassLoader().getResource("oppgave_endret.json")).toURI());
        var oppgaveJsonString = Files.readString(path);
        var treeNode = new ObjectMapper().readTree(oppgaveJsonString.replaceAll("\\$id", oppgaveID)
            .replaceAll("\\$fnr", FNR)
            .replaceAll("\\$versjonsnummer", versjonsNummer)
            .replaceAll("\\$rinasaksnummer", rinaSaksnummer)); //TODO: WHAT TO DO?

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper.treeToValue(treeNode, OppgaveKafkaAivenRecord.class);
    }
}
