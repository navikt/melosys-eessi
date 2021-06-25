package no.nav.melosys.eessi;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest(classes = {ComponentTestConfig.class, KafkaTestConfig.class })
@TestPropertySource(locations = "/kafka-test.properties")
public abstract class ComponentTestBase {

    static final LocalDate FØDSELSDATO = LocalDate.of(2000, 1, 1);
    static final String STATSBORGERSKAP = "NO";
    static final String FNR = "25068420779";
    static final String AKTOER_ID = "1234567890123";
    static final String RINA_SAKSNUMMER = Integer.toString(new Random().nextInt(100000));

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
        when(euxConsumer.hentBuC(anyString())).thenReturn(mockData.buc("rinadokumentid"));
        when(euxConsumer.hentSedMedVedlegg(anyString(), anyString())).thenReturn(mockData.sedMedVedlegg());
        when(journalpostapiConsumer.opprettJournalpost(any(OpprettJournalpostRequest.class), anyBoolean())).thenReturn(mockData.journalpostResponse());
        when(dokumenttypeIdConsumer.hentDokumenttypeId(anyString(), anyString())).thenReturn(new DokumenttypeIdDto("dokumenttypeId"));
        when(dokumenttypeInfoConsumer.hentDokumenttypeInfo(anyString())).thenReturn(mockData.dokumentTypeInfoDto());
    }

    @SneakyThrows
    protected void mockPerson(String ident, String aktørID) {
        when(pdlConsumer.hentIdenter(ident)).thenReturn(mockData.lagPDLIdentListe(ident, aktørID));
        when(pdlConsumer.hentIdenter(aktørID)).thenReturn(mockData.lagPDLIdentListe(ident, aktørID));
        when(pdlConsumer.hentPerson(ident)).thenReturn(mockData.pdlPerson(FØDSELSDATO, STATSBORGERSKAP));
    }

    List<ConsumerRecord<Object, Object>> hentRecords() {
        return kafkaTestConsumer.getRecords().stream().filter(ConsumerRecordPredicates.topic("privat-melosys-eessi-v1-local")).collect(Collectors.toList());
    }
}
