package no.nav.melosys.eessi.identifisering

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.melosys.eessi.integration.PersonFasade
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.buc.BUC
import no.nav.melosys.eessi.models.buc.Creator
import no.nav.melosys.eessi.models.buc.Document
import no.nav.melosys.eessi.models.buc.Organisation
import no.nav.melosys.eessi.models.person.Kjønn
import no.nav.melosys.eessi.models.person.PersonModell
import no.nav.melosys.eessi.models.person.UtenlandskId
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.nav.*
import no.nav.melosys.eessi.service.eux.EuxService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.time.ZonedDateTime

@ExtendWith(MockKExtension::class)
internal class IdentifiseringKontrollServiceTest {

    @MockK
    private lateinit var euxService: EuxService

    @MockK
    private lateinit var personFasade: PersonFasade

    @MockK(relaxed = true)
    private lateinit var personSokMetrikker: PersonSokMetrikker

    private lateinit var identifiseringKontrollService: IdentifiseringKontrollService

    private val sed = SED()

    private val fødselsdato: LocalDate = LocalDate.now()
    private val sedPerson = Person()
    private val utenlandskId = "41545325643"
    private val avsenderLand: String = "SE"
    private val statsborgerskap = Statsborgerskap()
    private val aktørID = "53254341"

    private val rinaSaksnummer = "432534"
    private val dokumentId = "abcdefghijkl1"

    private val buc = lagBuc()
    private val personBuilder = PersonModell.builder()

    @BeforeEach
    fun setup() {
        identifiseringKontrollService = IdentifiseringKontrollService(personFasade, euxService, personSokMetrikker)

        val utenlandskPin = Pin(utenlandskId, avsenderLand, null)
        statsborgerskap.land = avsenderLand

        sedPerson.foedselsdato = fødselsdato.toString()
        sedPerson.kjoenn = no.nav.melosys.eessi.models.sed.nav.Kjønn.K
        sedPerson.pin = setOf(utenlandskPin)
        sedPerson.statsborgerskap = setOf(statsborgerskap)

        sed.nav = Nav(
            bruker = Bruker(
                person = sedPerson
            )
        )
        sed.sedType = SedType.A009.name

        every { euxService.hentBuc(rinaSaksnummer) } answers {
            print("rinaSaksnummer: $rinaSaksnummer buc: $buc")
            buc
        }
        every { euxService.hentSed(rinaSaksnummer, dokumentId) } returns sed

        personBuilder
            .kjønn(Kjønn.KVINNE)
            .fødselsdato(fødselsdato)
            .statsborgerskapLandkodeISO2(setOf(avsenderLand))
            .utenlandskId(setOf(UtenlandskId(utenlandskId, avsenderLand)))
    }

    private fun lagBuc(): BUC = BUC(
        creator = Creator(
            organisation = Organisation(
                countryCode = avsenderLand
            )
        ),
        documents = listOf(
            Document(
                id = dokumentId,
                direction = "IN",
                status = "CREATED",
                creationDate = ZonedDateTime.now()
            )
        )
    )

    @Test
    fun `kontrollerIdentifisertPerson - person samstemmer med SED, identifisert`() {
        every { personFasade.hentPerson(aktørID) } returns personBuilder.build()

        val resultat = identifiseringKontrollService.kontrollerIdentifisertPerson(aktørID, rinaSaksnummer, 1)

        resultat.erIdentifisert() shouldBe true
        resultat.begrunnelser.shouldBeEmpty()
    }

    @Test
    fun `kontrollerIdentifisertPerson - person med ukjent kjønn, identifisert`() {
        sedPerson.kjoenn = no.nav.melosys.eessi.models.sed.nav.Kjønn.U
        every { personFasade.hentPerson(aktørID) } returns personBuilder.build()

        val resultat = identifiseringKontrollService.kontrollerIdentifisertPerson(aktørID, rinaSaksnummer, 1)

        resultat.erIdentifisert() shouldBe true
        resultat.begrunnelser.shouldBeEmpty()
    }

    @Test
    fun `kontrollerIdentifisertPerson - person overstyrt av ID og fordeling, identifisert`() {
        sedPerson.foedselsdato = LocalDate.now().minusMonths(3).toString()
        every { personFasade.hentPerson(aktørID) } returns personBuilder.build()

        val resultat = identifiseringKontrollService.kontrollerIdentifisertPerson(aktørID, rinaSaksnummer, 2)

        resultat.erIdentifisert() shouldBe true
        resultat.begrunnelser.shouldBeEmpty()
    }

    @Test
    fun `kontrollerIdentifisertPerson - person har ikke riktig statsborgerskap, ikke identifisert`() {
        every { personFasade.hentPerson(aktørID) } returns personBuilder.statsborgerskapLandkodeISO2(setOf("DK")).build()

        val resultat = identifiseringKontrollService.kontrollerIdentifisertPerson(aktørID, rinaSaksnummer, 1)

        resultat.erIdentifisert() shouldBe false
        resultat.begrunnelser shouldContain IdentifiseringsKontrollBegrunnelse.STATSBORGERSKAP
    }

    @Test
    fun `kontrollerIdentifisertPerson - person har ikke riktig fødselsdato, ikke identifisert`() {
        every { personFasade.hentPerson(aktørID) } returns personBuilder.fødselsdato(LocalDate.now().minusYears(3)).build()

        val resultat = identifiseringKontrollService.kontrollerIdentifisertPerson(aktørID, rinaSaksnummer, 1)

        resultat.erIdentifisert() shouldBe false
        resultat.begrunnelser shouldContain IdentifiseringsKontrollBegrunnelse.FØDSELSDATO
    }

    @Test
    fun `kontrollerIdentifisertPerson - person har ikke riktig utenlandsk id, ikke identifisert`() {
        every { personFasade.hentPerson(aktørID) } returns personBuilder.utenlandskId(setOf(UtenlandskId("feil-pin", avsenderLand))).build()

        val resultat = identifiseringKontrollService.kontrollerIdentifisertPerson(aktørID, rinaSaksnummer, 1)

        resultat.erIdentifisert() shouldBe false
        resultat.begrunnelser shouldContain IdentifiseringsKontrollBegrunnelse.UTENLANDSK_ID
    }
}
