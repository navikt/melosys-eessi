package no.nav.melosys.eessi;

import java.time.LocalDate;

import lombok.SneakyThrows;
import no.nav.melosys.eessi.integration.dokkat.DokumenttypeIdConsumer;
import no.nav.melosys.eessi.integration.dokkat.DokumenttypeInfoConsumer;
import no.nav.melosys.eessi.integration.dokkat.dto.DokumenttypeIdDto;
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer;
import no.nav.melosys.eessi.integration.journalpostapi.JournalpostapiConsumer;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostRequest;
import no.nav.melosys.eessi.integration.oppgave.OppgaveConsumer;
import no.nav.melosys.eessi.integration.saf.SafConsumer;
import no.nav.melosys.eessi.integration.sak.SakConsumer;
import no.nav.melosys.eessi.integration.tps.aktoer.AktoerConsumer;
import no.nav.melosys.eessi.integration.tps.person.PersonConsumer;
import no.nav.melosys.eessi.integration.tps.personsok.PersonsokConsumer;
import no.nav.melosys.eessi.repository.SedMottattRepository;
import no.nav.melosys.utils.KafkaTestConfig;
import no.nav.melosys.utils.KafkaTestConsumer;
import no.nav.melosys.utils.PostgresContainer;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
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

    private static final LocalDate FØDSELSDATO = LocalDate.of(2000, 1, 1);
    private static final String STATSBORGERSKAP = "NO";

    protected final MockData mockData = new MockData();

    @Autowired
    KafkaTestConsumer kafkaTestConsumer;

    @MockBean
    EuxConsumer euxConsumer;

    @MockBean
    PersonConsumer personConsumer;

    @MockBean
    AktoerConsumer aktoerConsumer;

    @MockBean
    PersonsokConsumer personsokConsumer;

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

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    SedMottattRepository sedMottattRepository;

    protected ProducerRecord<String, Object> createProducerRecord(String fnr) {
        return new ProducerRecord<>("eessi-basis-sedMottatt-v1", "key", mockData.sedHendelse(fnr));
    }

    @Container
    public static PostgresContainer DB = PostgresContainer.getInstance();


    @BeforeEach
    public void setup() {
        when(euxConsumer.hentBuC(anyString())).thenReturn(mockData.buc("rinadokumentid"));
        when(euxConsumer.hentSedMedVedlegg(anyString(), anyString())).thenReturn(mockData.sedMedVedlegg());
        when(journalpostapiConsumer.opprettJournalpost(any(OpprettJournalpostRequest.class), anyBoolean())).thenReturn(mockData.journalpostResponse());
        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(mockData.sed(FØDSELSDATO, STATSBORGERSKAP));
        when(dokumenttypeIdConsumer.hentDokumenttypeId(anyString(), anyString())).thenReturn(new DokumenttypeIdDto("dokumenttypeId"));
        when(dokumenttypeInfoConsumer.hentDokumenttypeInfo(anyString())).thenReturn(mockData.dokumentTypeInfoDto());
    }

    @SneakyThrows
    protected void mockPerson(String ident, String aktørID) {
        when(personConsumer.hentPerson(argThat(req -> ((PersonIdent) req.getAktoer()).getIdent().getIdent().equals(ident))))
                .thenReturn(mockData.hentPersonResponse(ident, FØDSELSDATO, "NO"));
        when(aktoerConsumer.hentAktoerId(ident)).thenReturn(aktørID);
        when(aktoerConsumer.hentNorskIdent(aktørID)).thenReturn(ident);
    }
}
