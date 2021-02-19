package no.nav.melosys.eessi;

import java.time.LocalDate;

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
import no.nav.melosys.utils.KafkaTestConfig;
import no.nav.melosys.utils.KafkaTestConsumer;
import no.nav.melosys.utils.PostgresContainer;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest(classes = {ComponentTestConfig.class, KafkaTestConfig.class })
@TestPropertySource(locations = "/kafka-test.properties")
public abstract class ComponentTestBase {

    private static final String AKTOER_ID = "1234567890123";
    private static final LocalDate FØDSELSDATO = LocalDate.of(2000, 1, 1);
    private static final String STATSBORGERSKAP = "NO";

    protected final ComponentTestProvider componentTestProvider = new ComponentTestProvider();

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

    protected ProducerRecord<String, Object> createProducerRecord() {
        return new ProducerRecord<>("eessi-basis-sedMottatt-v1", "key", componentTestProvider.sedHendelse(AKTOER_ID));
    }

    @ClassRule
    public static PostgresContainer DB = PostgresContainer.getInstance();


    @BeforeEach
    public void setup() throws Exception {
        when(euxConsumer.hentBuC(anyString())).thenReturn(componentTestProvider.buc("rinadokumentid"));
        when(euxConsumer.hentSedMedVedlegg(anyString(), anyString())).thenReturn(componentTestProvider.sedMedVedlegg());
        when(journalpostapiConsumer.opprettJournalpost(any(OpprettJournalpostRequest.class), anyBoolean())).thenReturn(componentTestProvider.journalpostResponse());
        when(aktoerConsumer.hentAktoerId(anyString())).thenReturn(AKTOER_ID);
        when(personConsumer.hentPerson(any(HentPersonRequest.class))).thenReturn(componentTestProvider.hentPersonResponse(AKTOER_ID, FØDSELSDATO, "NO"));
        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(componentTestProvider.sed(FØDSELSDATO, STATSBORGERSKAP));
        when(dokumenttypeIdConsumer.hentDokumenttypeId(anyString(), anyString())).thenReturn(new DokumenttypeIdDto("dokumenttypeId"));
        when(dokumenttypeInfoConsumer.hentDokumenttypeInfo(anyString())).thenReturn(componentTestProvider.dokumentTypeInfoDto());
    }
}
