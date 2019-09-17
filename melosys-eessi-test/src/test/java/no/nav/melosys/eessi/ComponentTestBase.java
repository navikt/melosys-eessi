package no.nav.melosys.eessi;

import java.time.LocalDate;
import no.nav.dokkat.api.tkat022.DokumenttypeIdTo;
import no.nav.melosys.eessi.integration.dokkat.DokumenttypeIdConsumer;
import no.nav.melosys.eessi.integration.dokkat.DokumenttypeInfoConsumer;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.integration.gsak.oppgave.OppgaveConsumer;
import no.nav.melosys.eessi.integration.gsak.sak.SakConsumer;
import no.nav.melosys.eessi.integration.journalpostapi.JournalpostapiConsumer;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostRequest;
import no.nav.melosys.eessi.integration.tps.aktoer.AktoerConsumer;
import no.nav.melosys.eessi.integration.tps.person.PersonConsumer;
import no.nav.melosys.eessi.integration.tps.personsok.PersonsokConsumer;
import no.nav.melosys.utils.KafkaTestConfig;
import no.nav.melosys.utils.KafkaTestConsumer;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
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

    @Autowired
    EuxConsumer euxConsumer;

    @Autowired
    PersonConsumer personConsumer;

    @Autowired
    AktoerConsumer aktoerConsumer;

    @Autowired
    PersonsokConsumer personsokConsumer;

    @Autowired
    SakConsumer sakConsumer;

    @Autowired
    DokumenttypeIdConsumer dokumenttypeIdConsumer;

    @Autowired
    DokumenttypeInfoConsumer dokumenttypeInfoConsumer;

    @Autowired
    JournalpostapiConsumer journalpostapiConsumer;

    @Autowired
    OppgaveConsumer oppgaveConsumer;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    protected ProducerRecord<String, Object> createProducerRecord() {
        return new ProducerRecord<>("eessi-basis-sedMottatt-v1", "key", componentTestProvider.sedHendelse(AKTOER_ID));
    }

    @Before
    public void setup() throws Exception {
        when(euxConsumer.hentBuC(anyString())).thenReturn(componentTestProvider.buc("rinadokumentid"));
        when(euxConsumer.hentSedMedVedlegg(anyString(), anyString())).thenReturn(componentTestProvider.sedMedVedlegg());
        when(sakConsumer.opprettSak(anyString())).thenReturn(componentTestProvider.sak(AKTOER_ID));
        when(journalpostapiConsumer.opprettJournalpost(any(OpprettJournalpostRequest.class), anyBoolean())).thenReturn(componentTestProvider.journalpostResponse());
        when(aktoerConsumer.hentAktoerId(anyString())).thenReturn(AKTOER_ID);
        when(personConsumer.hentPerson(any(HentPersonRequest.class))).thenReturn(componentTestProvider.hentPersonResponse(AKTOER_ID, FØDSELSDATO, "NO"));
        when(euxConsumer.hentSed(anyString(), anyString())).thenReturn(componentTestProvider.sed(FØDSELSDATO, STATSBORGERSKAP));
        when(dokumenttypeIdConsumer.hentDokumenttypeId(anyString(), anyString())).thenReturn(new DokumenttypeIdTo("dokumenttypeId"));
        when(dokumenttypeInfoConsumer.hentDokumenttypeInfo(anyString())).thenReturn(componentTestProvider.dokumentTypeInfoToV4());
    }
}
