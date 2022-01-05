package no.nav.melosys.eessi;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import no.finn.unleash.Unleash;
import no.nav.melosys.eessi.integration.dokkat.DokumenttypeIdConsumer;
import no.nav.melosys.eessi.integration.dokkat.DokumenttypeInfoConsumer;
import no.nav.melosys.eessi.integration.dokkat.dto.DokumenttypeIdDto;
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer;
import no.nav.melosys.eessi.integration.journalpostapi.JournalpostapiConsumer;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostRequest;
import no.nav.melosys.eessi.integration.oppgave.OppgaveConsumer;
import no.nav.melosys.eessi.integration.pdl.PDLConsumer;
import no.nav.melosys.eessi.integration.saf.SafConsumer;
import no.nav.melosys.eessi.integration.sak.SakConsumer;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.kafka.producers.model.Periode;
import no.nav.melosys.eessi.repository.SedMottattRepository;
import no.nav.melosys.utils.ConsumerRecordPredicates;
import no.nav.melosys.utils.KafkaTestConfig;
import no.nav.melosys.utils.KafkaTestConsumer;
import no.nav.melosys.utils.PostgresContainer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest(classes = {ComponentTestConfig.class, KafkaTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/kafka-test.properties")
public abstract class ComponentTestBase {

    static final LocalDate FØDSELSDATO = LocalDate.of(2000, 1, 1);
    static final String STATSBORGERSKAP = "NO";
    static final String FNR = "25068420779";
    static final String AKTOER_ID = "1234567890123";
    static final String RINA_SAKSNUMMER = Integer.toString(new Random().nextInt(100000));
    static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    protected final MockData mockData = new MockData();

    @Autowired
    KafkaTestConsumer kafkaTestConsumer;

    @MockBean
    EuxConsumer euxConsumer;

    @MockBean
    SakConsumer sakConsumer;

    @MockBean
    DokumenttypeIdConsumer dokumenttypeIdConsumer;

    @MockBean
    DokumenttypeInfoConsumer dokumenttypeInfoConsumer;

    @MockBean
    JournalpostapiConsumer journalpostapiConsumer;

    @MockBean
    OppgaveConsumer oppgaveConsumer;

    @MockBean
    SafConsumer safConsumer;

    @MockBean
    PDLConsumer pdlConsumer;

    @Autowired
    Unleash unleash;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    SedMottattRepository sedMottattRepository;

    protected ProducerRecord<String, Object> lagSedMottattRecord(SedHendelse sedHendelse) {
        return new ProducerRecord<>("eessi-basis-sedMottatt-v1", "key", sedHendelse);
    }

    @Container
    public static PostgresContainer DB = PostgresContainer.getInstance();


    @BeforeEach
    public void setup() {
        when(euxConsumer.hentBUC(anyString())).thenReturn(mockData.buc("rinadokumentid"));
        when(euxConsumer.hentSedMedVedlegg(anyString(), anyString())).thenReturn(mockData.sedMedVedlegg());
        when(dokumenttypeIdConsumer.hentDokumenttypeId(anyString(), anyString())).thenReturn(new DokumenttypeIdDto("dokumenttypeId"));
        when(dokumenttypeInfoConsumer.hentDokumenttypeInfo(anyString())).thenReturn(mockData.dokumentTypeInfoDto());
        when(journalpostapiConsumer.opprettJournalpost(any(OpprettJournalpostRequest.class), anyBoolean()))
            .thenAnswer(a -> mockData.journalpostResponse(a.getArgument(1, Boolean.class)));
    }

    protected void mockPerson(String ident, String aktørID) {
        mockPerson(ident, aktørID, FØDSELSDATO, STATSBORGERSKAP);
    }

    protected void mockPerson(String ident, String aktørID, LocalDate fødselsdato, String statsborgerskap) {
        mockHentPerson(ident, aktørID, fødselsdato, statsborgerskap);
        mockHentIdenter(ident, aktørID);
    }

    private void mockHentPerson(String ident, String aktørID, LocalDate fødselsdato, String statsborgerskap) {
        var pdlPerson = mockData.pdlPerson(fødselsdato, statsborgerskap);
        when(pdlConsumer.hentPerson(ident)).thenReturn(pdlPerson);
        when(pdlConsumer.hentPerson(aktørID)).thenReturn(pdlPerson);
    }

    protected void mockHentIdenter(String ident, String aktørID) {
        var pdlIdentListe = mockData.lagPDLIdentListe(ident, aktørID);
        when(pdlConsumer.hentIdenter(ident)).thenReturn(pdlIdentListe);
        when(pdlConsumer.hentIdenter(aktørID)).thenReturn(pdlIdentListe);
    }

    List<MelosysEessiMelding> hentMelosysEessiRecords() {
        return kafkaTestConsumer.getRecords()
            .stream()
            .filter(ConsumerRecordPredicates.topic("privat-melosys-eessi-v1-local"))
            .map(ConsumerRecord::value)
            .map(this::tilMelosysEessiMelding)
            .collect(Collectors.toList());
    }

    @SneakyThrows
    MelosysEessiMelding tilMelosysEessiMelding(String value) {
        return objectMapper.readValue(value, MelosysEessiMelding.class);
    }

    void assertMelosysEessiMelding(Collection<MelosysEessiMelding> melosysEessiMelding, int forventetStørrelse) {
        assertThat(melosysEessiMelding)
            .hasSize(forventetStørrelse)
            .allMatch(eessiMelding ->
                eessiMelding.getPeriode().equals(new Periode(LocalDate.parse("2019-06-01"), LocalDate.parse("2019-12-01")))
                    && eessiMelding.getJournalpostId().equals("1")
                    && eessiMelding.getAktoerId().equals(AKTOER_ID)
            );
    }

    protected ProducerRecord<String, Object> lagOppgaveIdentifisertRecord(String oppgaveID, String fnr, String versjon, String rinaSaksnummer) {
        return new ProducerRecord<>("oppgave-endret", "key", oppgaveEksempel(oppgaveID, fnr, AKTOER_ID, versjon, rinaSaksnummer));
    }

    @SneakyThrows
    private Object oppgaveEksempel(String oppgaveID, String ident, String aktørID, String versjonsNummer, String rinaSaksnummer) {
        var path = Paths.get(Objects.requireNonNull(this.getClass().getClassLoader().getResource("oppgave_endret.json")).toURI());
        var oppgaveJsonString = Files.readString(path);
        return new ObjectMapper().readTree(oppgaveJsonString.replaceAll("\\$id", oppgaveID)
            .replaceAll("\\$fnr", ident)
            .replaceAll("\\$aktoerid", aktørID)
            .replaceAll("\\$versjonsnummer", versjonsNummer)
            .replaceAll("\\$rinasaksnummer", rinaSaksnummer));
    }
}
