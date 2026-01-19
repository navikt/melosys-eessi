package no.nav.melosys.eessi

import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import io.getunleash.FakeUnleash
import io.getunleash.Unleash
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.identifisering.OppgaveKafkaAivenRecord
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer
import no.nav.melosys.eessi.integration.journalpostapi.JournalpostapiConsumer
import no.nav.melosys.eessi.integration.oppgave.OppgaveConsumer
import no.nav.melosys.eessi.integration.pdl.PDLConsumer
import no.nav.melosys.eessi.integration.pdl.PdlWebConsumer
import no.nav.melosys.eessi.integration.saf.SafConsumer
import no.nav.melosys.eessi.integration.sak.SakConsumer
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding
import no.nav.melosys.utils.ConsumerRecordPredicates
import no.nav.melosys.utils.KafkaTestConfig
import no.nav.melosys.utils.KafkaTestConsumer
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

@ExtendWith(org.springframework.test.context.junit.jupiter.SpringExtension::class)
@ActiveProfiles("test")
@SpringBootTest(
    classes = [ComponentTestConfig::class, KafkaTestConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource("/kafka-test.properties")
@EmbeddedKafka(
    controlledShutdown = true,
    partitions = 1,
    topics = [
        ComponentTestBaseKotlin.EESSIBASIS_SEDMOTTATT_V_1,
        ComponentTestBaseKotlin.EESSIBASIS_SEDSENDT_V_1,
        ComponentTestBaseKotlin.OPPGAVE_ENDRET,
        ComponentTestBaseKotlin.TEAMMELOSYS_EESSI_V_1_LOCAL
    ],
    brokerProperties = [
        "offsets.topic.replication.factor=1",
        "transaction.state.log.replication.factor=1",
        "transaction.state.log.min.isr=1"
    ]
)
@EnableMockOAuth2Server
abstract class ComponentTestBaseKotlin : PostgresTestContainerBase() {
    protected val mockData = MockData()
    @Autowired
    lateinit var unleash: Unleash

    @Autowired
    lateinit var kafkaTestConsumer: KafkaTestConsumer

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Autowired(required = false)
    lateinit var jsonMapper: JsonMapper

    @MockkBean(relaxed = true)
    lateinit var euxConsumer: EuxConsumer

    @MockkBean(relaxed = true)
    lateinit var sakConsumer: SakConsumer

    @MockkBean(relaxed = true)
    lateinit var journalpostapiConsumer: JournalpostapiConsumer

    @MockkBean(relaxed = true)
    lateinit var oppgaveConsumer: OppgaveConsumer

    @MockkBean(relaxed = true)
    lateinit var safConsumer: SafConsumer

    @MockkBean(relaxed = true)
    lateinit var pdlConsumer: PDLConsumer

    @MockkBean(relaxed = true)
    lateinit var pdlWebConsumer: PdlWebConsumer

    val fakeUnleash: FakeUnleash by lazy {
        unleash.shouldBeInstanceOf<FakeUnleash>()
    }

    protected fun lagSedMottattRecord(sedHendelse: SedHendelse): ProducerRecord<String, Any> =
        ProducerRecord(EESSIBASIS_SEDMOTTATT_V_1, "key", sedHendelse)

    protected fun lagOppgaveIdentifisertRecord(
        oppgaveID: String,
        versjon: String,
        rinaSaksnummer: String
    ): ProducerRecord<String, Any> =
        ProducerRecord(OPPGAVE_ENDRET, "key", oppgaveEksempel(oppgaveID, versjon, rinaSaksnummer))

    private fun oppgaveEksempel(
        oppgaveID: String,
        versjon: String,
        rinaSaksnummer: String
    ): OppgaveKafkaAivenRecord {
        val url = requireNotNull(javaClass.classLoader.getResource("oppgave_endret.json"))
        val template = Files.readString(Paths.get(url.toURI()))
        val json = template
            .replace("\$id", oppgaveID)
            .replace("\$fnr", FNR)
            .replace("\$versjonsnummer", versjon)
            .replace("\$rinasaksnummer", rinaSaksnummer)
        return jsonMapper.readValue(json)
    }

    @BeforeEach
    fun setup() {
        fakeUnleash.enableAll()
        io.mockk.every { euxConsumer.hentBUC(any()) } returns mockData.buc("rinadokumentid")
        io.mockk.every { euxConsumer.hentSedMedVedlegg(any(), any()) } returns mockData.sedMedVedlegg()
        io.mockk.every { journalpostapiConsumer.opprettJournalpost(any(), any()) } answers {
            mockData.journalpostResponse(secondArg())
        }
    }

    protected fun mockPerson(
        fødselsdato: LocalDate = FØDSELSDATO,
        statsborgerskap: String = STATSBORGERSKAP
    ) {
        val pdlPerson = mockData.pdlPerson(fødselsdato, statsborgerskap)
        io.mockk.every { pdlConsumer.hentPerson(FNR) } returns pdlPerson
        io.mockk.every { pdlConsumer.hentPerson(AKTOER_ID) } returns pdlPerson
        mockHentIdenter()
    }

    protected fun mockHentIdenter() {
        val identer = mockData.lagPDLIdentListe(FNR, AKTOER_ID)
        io.mockk.every { pdlConsumer.hentIdenter(FNR) } returns identer
        io.mockk.every { pdlConsumer.hentIdenter(AKTOER_ID) } returns identer
    }

    fun hentMelosysEessiRecords(): List<MelosysEessiMelding> =
        kafkaTestConsumer.records.orEmpty()
            .filter { ConsumerRecordPredicates.topic(TEAMMELOSYS_EESSI_V_1_LOCAL).test(it) }
            .map { jsonMapper.readValue(it.value(), MelosysEessiMelding::class.java) }

    companion object {
        const val EESSIBASIS_SEDMOTTATT_V_1 = "eessibasis-sedmottatt-v1"
        const val EESSIBASIS_SEDSENDT_V_1 = "eessibasis-sedsendt-v1"
        const val OPPGAVE_ENDRET = "oppgavehandtering.oppgavehendelse-v1"
        const val TEAMMELOSYS_EESSI_V_1_LOCAL = "teammelosys.eessi.v1-local"

        val FØDSELSDATO: LocalDate = LocalDate.of(2000, 1, 1)
        const val STATSBORGERSKAP = "NO"
        const val FNR = "25068420779"
        const val AKTOER_ID = "1234567890123"
    }
}
