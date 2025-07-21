package no.nav.melosys.eessi.identifisering

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import no.nav.melosys.eessi.integration.pdl.PDLService
import no.nav.melosys.eessi.models.exception.NotFoundException
import no.nav.melosys.eessi.models.person.PersonModell
import no.nav.melosys.eessi.service.personsok.PersonSokResponse
import no.nav.melosys.eessi.service.personsok.PersonsokKriterier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import kotlin.test.assertEquals

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

        val resultat = personSok.søkEtterPerson(personsoekKriterier())

        assertThat(resultat.personIdentifisert()).isTrue()
        assertThat(resultat.ident).isEqualTo(IDENT)
        assertThat(resultat.begrunnelse).isEqualTo(SoekBegrunnelse.IDENTIFISERT)
    }

    @Test
    fun `søkEtterPerson - feil fødselsdato - forvent feil fødselsdato`() {
        every { personFasade.hentPerson(IDENT) } returns lagPersonModell(false)
        every { personFasade.soekEtterPerson(any()) } returns listOf(lagPersonSøkResponse())

        val resultat = personSok.søkEtterPerson(personsoekKriterier(LocalDate.of(2000, 1, 2)))

        assertThat(resultat.personIdentifisert()).isFalse()
        assertThat(resultat.ident).isNull()
        assertThat(resultat.begrunnelse).isEqualTo(SoekBegrunnelse.FEIL_FOEDSELSDATO)
    }

    @Test
    fun `søkEtterPerson - feil statsborgerskap - forvent feil statsborgerskap`() {
        every { personFasade.hentPerson(IDENT) } returns lagPersonModell(false)
        every { personFasade.soekEtterPerson(any()) } returns listOf(lagPersonSøkResponse())

        val resultat = personSok.søkEtterPerson(personsoekKriterier(LocalDate.now(), emptySet<String>()))

        assertThat(resultat.personIdentifisert()).isFalse()
        assertThat(resultat.ident).isNull()
        assertThat(resultat.begrunnelse).isEqualTo(SoekBegrunnelse.FEIL_STATSBORGERSKAP)
    }

    @Test
    fun `søkEtterPerson - ingen treff - forvent ingen treff`() {
        every { personFasade.soekEtterPerson(any()) } returns emptyList()

        val resultat = personSok.søkEtterPerson(personsoekKriterier())

        assertThat(resultat.personIdentifisert()).isFalse()
        assertThat(resultat.begrunnelse).isEqualTo(SoekBegrunnelse.INGEN_TREFF)
    }

    @Test
    fun `søkEtterPerson - flere treff - forvent flere treff`() {
        every { personFasade.soekEtterPerson(any()) } returns listOf(PersonSokResponse(), PersonSokResponse())

        val resultat = personSok.søkEtterPerson(personsoekKriterier())

        assertThat(resultat.personIdentifisert()).isFalse()
        assertThat(resultat.ident).isNull()
        assertThat(resultat.begrunnelse).isEqualTo(SoekBegrunnelse.FLERE_TREFF)
    }

    @Test
    fun `søkEtterPerson - person ikke funnet i ITPS - forvent fnr ikke funnet`() {
        every { personFasade.hentPerson(any()) } throws NotFoundException("Fnr ikke funnet")
        every { personFasade.soekEtterPerson(any()) } returns listOf(lagPersonSøkResponse())

        val resultat = personSok.søkEtterPerson(personsoekKriterier())

        assertThat(resultat.personIdentifisert()).isFalse()
        assertThat(resultat.begrunnelse).isEqualTo(SoekBegrunnelse.FNR_IKKE_FUNNET)
    }

    @Test
    fun `søkEtterPerson - person er opphørt - forvent person opphørt`() {
        every { personFasade.hentPerson(any()) } returns lagPersonModell(true)
        every { personFasade.soekEtterPerson(any()) } returns listOf(lagPersonSøkResponse())

        val resultat = personSok.søkEtterPerson(personsoekKriterier())

        assertThat(resultat.personIdentifisert()).isFalse()
        assertThat(resultat.begrunnelse).isEqualTo(SoekBegrunnelse.PERSON_OPPHORT)
    }

    @Test
    fun `vurderPerson skal returnerne FEIL_NAVN når etternavn ikke matcher`() {
        val ident = "12345678901"
        val personFraPDL = lagPersonModell(false)
        val søkekriterier = personsoekKriterier().apply { etternavn = "FeilEtternavn" }

        every { personFasade.hentPerson(ident) } returns personFraPDL

        val resultat = personSok.vurderPerson(ident, søkekriterier)

        assertEquals(SoekBegrunnelse.FEIL_NAVN, resultat.begrunnelse)
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
        val response = PersonSokResponse()
        response.ident = IDENT
        return response
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
