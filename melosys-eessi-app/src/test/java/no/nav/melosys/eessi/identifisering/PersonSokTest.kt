package no.nav.melosys.eessi.identifisering

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import no.nav.melosys.eessi.integration.pdl.PDLService
import no.nav.melosys.eessi.models.exception.NotFoundException
import no.nav.melosys.eessi.models.person.PersonModell
import no.nav.melosys.eessi.service.personsok.PersonSokResponse
import no.nav.melosys.eessi.service.personsok.PersonsokKriterier
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class PersonSokTest {

    private val IDENT = "01058312345"
    private val defaultFødselsdato = LocalDate.of(2000, 1, 1)
    private val defaultStatsborgerskap = setOf("NO")

    private val personFasade: PDLService = mockk()
    private lateinit var personSok: PersonSok

    @BeforeEach
    fun setup() {
        personSok = PersonSok(personFasade)
    }

    @Test
    fun `søkEtterPerson - ett treff med korrekte opplysninger - forvent ident identifisert`() {
        every { personFasade.hentPerson(IDENT) } returns lagPersonModell(false)
        every { personFasade.soekEtterPerson(any()) } returns listOf(lagPersonSøkResponse())

        personSok.søkEtterPerson(personsoekKriterier()).run {
            personIdentifisert().shouldBeTrue()
            ident shouldBe IDENT
            begrunnelse shouldBe SoekBegrunnelse.IDENTIFISERT
        }
    }

    @Test
    fun `søkEtterPerson - feil fødselsdato - forvent feil fødselsdato`() {
        every { personFasade.hentPerson(IDENT) } returns lagPersonModell(false)
        every { personFasade.soekEtterPerson(any()) } returns listOf(lagPersonSøkResponse())

        personSok.søkEtterPerson(personsoekKriterier(LocalDate.of(2000, 1, 2))).run {
            personIdentifisert().shouldBeFalse()
            ident.shouldBeNull()
            begrunnelse shouldBe SoekBegrunnelse.FEIL_FOEDSELSDATO
        }
    }

    @Test
    fun `søkEtterPerson - feil statsborgerskap - forvent feil statsborgerskap`() {
        every { personFasade.hentPerson(IDENT) } returns lagPersonModell(false)
        every { personFasade.soekEtterPerson(any()) } returns listOf(lagPersonSøkResponse())

        personSok.søkEtterPerson(personsoekKriterier(LocalDate.now(), emptySet<String>())).run {
            personIdentifisert().shouldBeFalse()
            ident.shouldBeNull()
            begrunnelse shouldBe SoekBegrunnelse.FEIL_STATSBORGERSKAP
        }
    }

    @Test
    fun `søkEtterPerson - ingen treff - forvent ingen treff`() {
        every { personFasade.soekEtterPerson(any()) } returns emptyList()

        personSok.søkEtterPerson(personsoekKriterier()).run {
            personIdentifisert().shouldBeFalse()
            begrunnelse shouldBe SoekBegrunnelse.INGEN_TREFF
        }
    }

    @Test
    fun `søkEtterPerson - flere treff - forvent flere treff`() {
        every { personFasade.soekEtterPerson(any()) } returns listOf(PersonSokResponse(), PersonSokResponse())

        personSok.søkEtterPerson(personsoekKriterier()).run {
            personIdentifisert().shouldBeFalse()
            ident.shouldBeNull()
            begrunnelse shouldBe SoekBegrunnelse.FLERE_TREFF
        }
    }

    @Test
    fun `søkEtterPerson - person ikke funnet i ITPS - forvent fnr ikke funnet`() {
        every { personFasade.hentPerson(any()) } throws NotFoundException("Fnr ikke funnet")
        every { personFasade.soekEtterPerson(any()) } returns listOf(lagPersonSøkResponse())

        personSok.søkEtterPerson(personsoekKriterier()).run {
            personIdentifisert().shouldBeFalse()
            begrunnelse shouldBe SoekBegrunnelse.FNR_IKKE_FUNNET
        }
    }

    @Test
    fun `søkEtterPerson - person er opphørt - forvent person opphørt`() {
        every { personFasade.hentPerson(any()) } returns lagPersonModell(true)
        every { personFasade.soekEtterPerson(any()) } returns listOf(lagPersonSøkResponse())

        personSok.søkEtterPerson(personsoekKriterier()).run {
            personIdentifisert().shouldBeFalse()
            begrunnelse shouldBe SoekBegrunnelse.PERSON_OPPHORT
        }
    }

    @Test
    fun `vurderPerson skal returnerne FEIL_NAVN når etternavn ikke matcher`() {
        val ident = "12345678901"
        val personFraPDL = lagPersonModell(false)
        val søkekriterier = personsoekKriterier().apply { etternavn = "FeilEtternavn" }

        every { personFasade.hentPerson(ident) } returns personFraPDL

        personSok.vurderPerson(ident, søkekriterier).run {
            begrunnelse shouldBe SoekBegrunnelse.FEIL_NAVN
        }
    }

    private fun personsoekKriterier(
        fødselsdato: LocalDate = defaultFødselsdato,
        statsborgerskap: Collection<String> = defaultStatsborgerskap
    ): PersonsokKriterier =
        PersonsokKriterier.builder()
            .fornavn("Fornavn")
            .etternavn("Etternavn")
            .foedselsdato(fødselsdato)
            .statsborgerskapISO2(statsborgerskap)
            .build()

    private fun lagPersonSøkResponse(): PersonSokResponse {
        return PersonSokResponse().apply {ident = IDENT}
    }

    private fun lagPersonModell(erOpphørt: Boolean): PersonModell =
        PersonModell.PersonModellBuilder()
            .ident(IDENT)
            .fornavn("Fornavn")
            .etternavn("Etternavn")
            .fødselsdato(defaultFødselsdato)
            .statsborgerskapLandkodeISO2(defaultStatsborgerskap)
            .erOpphørt(erOpphørt)
            .build()
}
