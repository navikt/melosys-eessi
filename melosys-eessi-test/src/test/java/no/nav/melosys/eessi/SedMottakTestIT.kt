package no.nav.melosys.eessi

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import no.nav.melosys.eessi.integration.oppgave.HentOppgaveDto
import no.nav.melosys.eessi.integration.pdl.dto.PDLIdent
import no.nav.melosys.eessi.integration.pdl.dto.PDLIdentGruppe
import no.nav.melosys.eessi.integration.pdl.dto.PDLSokHit
import no.nav.melosys.eessi.integration.pdl.dto.PDLSokPerson
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding
import no.nav.melosys.eessi.kafka.producers.model.Periode
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.models.sed.nav.*
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository
import no.nav.melosys.eessi.repository.SedMottattLager
import no.nav.melosys.eessi.repository.SedMottattLagerRepository
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.LocalDate
import java.util.*

class SedMottakTestIT : ComponentTestBaseKotlin() {

    @Autowired
    lateinit var bucIdentifiseringOppgRepository: BucIdentifiseringOppgRepository

    @Autowired
    lateinit var sedMottattHendelseRepository: SedMottattHendelseRepository

    @Autowired
    lateinit var sedMottattStorageRepository: SedMottattLagerRepository

    private val rinaSaksnummer = Random().nextInt(100000).toString()

    @Test
    fun `sed mottatt med fnr blir identifisert og publiseres på Kafka`() {
        val sedID = UUID.randomUUID().toString()
        every { euxConsumer.hentSed(any(), any()) } returns mockData.sed(FØDSELSDATO, STATSBORGERSKAP, FNR)

        mockPerson()

        kafkaTestConsumer.reset(2)
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID, FNR))).get()
        kafkaTestConsumer.doWait(5_000L)

        assertMelosysEessiMelding(hentMelosysEessiRecords(), 1)
    }

    @Test
    fun `sed mottatt med sektorKode H ignoreres`() {
        val sedID = UUID.randomUUID().toString()

        kafkaTestConsumer.reset(1)
        val sedHendelse = mockData.sedHendelse(rinaSaksnummer, sedID, FNR).apply {
            sektorKode = "H"
        }
        kafkaTemplate.send(lagSedMottattRecord(sedHendelse)).get()
        kafkaTestConsumer.doWait(5_000L)

        verify { euxConsumer wasNot Called }
    }

    @Test
    fun `sed mottatt uten fnr søker identifisering og publiseres på Kafka`() {
        val sedID = UUID.randomUUID().toString()
        every { euxConsumer.hentSed(any(), any()) } returns mockData.sed(FØDSELSDATO, STATSBORGERSKAP, null)

        val pdlSøkPerson = PDLSokPerson().apply {
            hits = setOf(PDLSokHit().apply {
                identer = setOf(PDLIdent(PDLIdentGruppe.FOLKEREGISTERIDENT, FNR))
            })
        }
        every { pdlConsumer.søkPerson(any()) } returns pdlSøkPerson
        mockPerson()

        kafkaTestConsumer.reset(2)
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID, null))).get()
        kafkaTestConsumer.doWait(5_000L)

        assertMelosysEessiMelding(hentMelosysEessiRecords(), 1)
    }

    @Test
    fun `to sed mottatt uten fnr oppretter identifiseringsoppgave og reagerer på endret oppgave`() {
        val sedID1 = UUID.randomUUID().toString()
        val sedID2 = UUID.randomUUID().toString()
        val oppgaveID = Random().nextInt(100000).toString()
        val oppgaveDto = HentOppgaveDto(oppgaveID, "AAPEN", 1).apply { status = "OPPRETTET" }

        mockPerson()
        every { euxConsumer.hentSed(any(), any()) } returns mockData.sed(FØDSELSDATO, STATSBORGERSKAP, null)
        every { pdlConsumer.søkPerson(any()) } returns PDLSokPerson()
        every { oppgaveConsumer.opprettOppgave(any()) } returns oppgaveDto
        every { oppgaveConsumer.hentOppgave(oppgaveID) } returns oppgaveDto

        kafkaTestConsumer.reset(2)
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID1, null))).get()
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID2, null))).get()
        kafkaTestConsumer.doWait(5_000L)

        await().atMost(Duration.ofSeconds(4)).pollInterval(Duration.ofSeconds(1))
            .until { sedMottattHendelseRepository.countAllByRinaSaksnummer(rinaSaksnummer) == 2 }

        await().atMost(Duration.ofSeconds(4)).untilAsserted {
            verify { oppgaveConsumer.opprettOppgave(any()) }
        }
        hentMelosysEessiRecords().shouldBeEmpty()
        bucIdentifiseringOppgRepository.findByOppgaveId(oppgaveID).shouldNotBeNull()

        kafkaTestConsumer.reset(3)
        kafkaTemplate.send(lagOppgaveIdentifisertRecord(oppgaveID, "1", rinaSaksnummer)).get()
        kafkaTestConsumer.doWait(5_000L)

        await().atMost(Duration.ofSeconds(4)).untilAsserted {
            verify { oppgaveConsumer.oppdaterOppgave(eq(oppgaveID), any()) }
        }
        assertMelosysEessiMelding(hentMelosysEessiRecords(), 2)
    }

    @Test
    fun `to sed mottatt med fnr publiseres på Kafka`() {
        val sedID1 = UUID.randomUUID().toString()
        val sedID2 = UUID.randomUUID().toString()
        mockPerson()

        every { euxConsumer.hentSed(any(), any()) } returns mockData.sed(FØDSELSDATO, STATSBORGERSKAP, FNR)
        every { pdlConsumer.søkPerson(any()) } returns PDLSokPerson()

        kafkaTestConsumer.reset(2)
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID1, FNR))).get()
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID2, FNR))).get()
        kafkaTestConsumer.doWait(5_000L)

        await().atMost(Duration.ofSeconds(4)).pollInterval(Duration.ofSeconds(1))
            .until { sedMottattHendelseRepository.countAllByRinaSaksnummer(rinaSaksnummer) == 2 }

        assertMelosysEessiMelding(hentMelosysEessiRecords(), 2)
    }

    @Test
    fun `to sed mottatt med treff på tredjelandsborger regel - skal lagre i sed`() {
        val sedID1 = UUID.randomUUID().toString()

        val sed = SED(
            sedType = SedType.A003.name,
            medlemskap = MedlemskapA003(
                vedtak = VedtakA003(land = "SE")
            ),
            nav = Nav(
                bruker = Bruker(
                    person = Person(
                        foedselsdato = "2019-06-01",
                        fornavn = "Fornavn",
                        etternavn = "Etternavn",
                        statsborgerskap = listOf(Statsborgerskap(land = "US"))
                    )
                ),
                arbeidssted = emptyList(),
                arbeidsland = emptyList()
            )
        )
        every { euxConsumer.hentSed(any(), any()) } returns sed

        kafkaTestConsumer.reset(1)
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID1, FNR))).get()
        kafkaTestConsumer.doWait(5_000L)

        await().atMost(Duration.ofSeconds(4)).pollInterval(Duration.ofSeconds(1))
            .until { sedMottattHendelseRepository.countAllByRinaSaksnummer(rinaSaksnummer) == 1 }

        assertMelosysEessiMelding(hentMelosysEessiRecords(), 0)

        sedMottattStorageRepository.findBySedId(sedID1)
            .shouldHaveSize(1)
            .single()
            .sed shouldBe sed

    }

    @Test
    fun `sed ikke identifisert oppgave identifiseres og markeres feil, første sed flyttes til ID og fordeling`() {
        val sedID1 = UUID.randomUUID().toString()
        val sedID2 = UUID.randomUUID().toString()
        val oppgaveID = Random().nextInt(100000).toString()
        val oppgaveDto = HentOppgaveDto(oppgaveID, "AAPEN", 1).apply { status = "OPPRETTET" }

        mockPerson(FØDSELSDATO.minusYears(1), "DK")
        every { euxConsumer.hentSed(any(), any()) } returns mockData.sed(FØDSELSDATO, STATSBORGERSKAP, null)
        every { pdlConsumer.søkPerson(any()) } returns PDLSokPerson()
        every { oppgaveConsumer.opprettOppgave(any()) } returns oppgaveDto
        every { oppgaveConsumer.hentOppgave(oppgaveID) } returns oppgaveDto

        kafkaTestConsumer.reset(1)
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID1, null))).get()
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID2, null))).get()
        kafkaTestConsumer.doWait(5_000L)

        await().atMost(Duration.ofSeconds(4)).pollInterval(Duration.ofSeconds(1))
            .until { sedMottattHendelseRepository.countAllByRinaSaksnummer(rinaSaksnummer) == 2 }

        await().atMost(Duration.ofSeconds(6)).untilAsserted {
            verify() { oppgaveConsumer.opprettOppgave(any()) }
        }

        hentMelosysEessiRecords().shouldBeEmpty()
        bucIdentifiseringOppgRepository.findByOppgaveId(oppgaveID).shouldNotBeNull()

        kafkaTestConsumer.reset(1)
        kafkaTemplate.send(lagOppgaveIdentifisertRecord(oppgaveID, "1", rinaSaksnummer)).get()
        kafkaTestConsumer.doWait(5_000L)

        await().atMost(Duration.ofSeconds(4)).untilAsserted {
            verify() { oppgaveConsumer.oppdaterOppgave(eq(oppgaveID), any()) }
        }
        hentMelosysEessiRecords().shouldBeEmpty()
    }

    @Test
    fun `identiske sed mottak meldinger behandles kun én gang`() {
        val sedID = UUID.randomUUID().toString()
        every { euxConsumer.hentSed(any(), any()) } returns mockData.sed(FØDSELSDATO, STATSBORGERSKAP, FNR)
        mockPerson()

        kafkaTestConsumer.reset(3)
        val hendelse = mockData.sedHendelse(rinaSaksnummer, sedID, FNR)
        kafkaTemplate.send(lagSedMottattRecord(hendelse)).get()
        kafkaTemplate.send(lagSedMottattRecord(hendelse)).get()
        kafkaTestConsumer.doWait(5_000L)

        await().atMost(Duration.ofSeconds(4)).pollInterval(Duration.ofSeconds(1))
            .until { sedMottattHendelseRepository.countAllByRinaSaksnummer(rinaSaksnummer) == 1 }

        assertMelosysEessiMelding(hentMelosysEessiRecords(), 1)
    }

    @Test
    fun `tredjelandsborger A003 uten Norge som arbeidssted setter skalJournalfoeres til false`() {
        val sedID = UUID.randomUUID().toString()
        
        val sed = SED(
            sedType = SedType.A003.name,
            medlemskap = MedlemskapA003(
                vedtak = VedtakA003(land = "DE") 
            ),
            nav = Nav(
                bruker = Bruker(
                    person = Person(
                        foedselsdato = "1990-01-01",
                        fornavn = "Test",
                        etternavn = "Person",
                        statsborgerskap = listOf(Statsborgerskap(land = "US")) 
                    )
                ),
                arbeidssted = emptyList(),
                arbeidsland = emptyList()
            )
        )
        every { euxConsumer.hentSed(any(), any()) } returns sed

        kafkaTestConsumer.reset(1)
        kafkaTemplate.send(lagSedMottattRecord(mockData.sedHendelse(rinaSaksnummer, sedID, null))).get()
        kafkaTestConsumer.doWait(5_000L)

        await().atMost(Duration.ofSeconds(7)).pollInterval(Duration.ofSeconds(1))
            .until { sedMottattHendelseRepository.countAllByRinaSaksnummer(rinaSaksnummer) == 1 }

        assertMelosysEessiMelding(hentMelosysEessiRecords(), 0)

        val storedLager = sedMottattStorageRepository.findBySedId(sedID)
        storedLager.shouldHaveSize(1)
        storedLager.first().sed shouldBe sed

        val sedMottattHendelser = sedMottattHendelseRepository.findAllByRinaSaksnummerSortedByMottattDatoDesc(rinaSaksnummer)
        sedMottattHendelser.shouldHaveSize(1)
        val sedMottattHendelse = sedMottattHendelser.first()
        
        sedMottattHendelse.skalJournalfoeres shouldBe false
    }

    fun assertMelosysEessiMelding(records: Collection<MelosysEessiMelding>, expectedSize: Int) {
        records.shouldHaveSize(expectedSize)
        records.forEach {
            it.periode shouldBe Periode(LocalDate.parse("2019-06-01"), LocalDate.parse("2019-12-01"))
            listOf(null, "1").shouldContain(it.journalpostId)
            it.aktoerId shouldBe AKTOER_ID
        }
    }
}
