import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.junit5.MockKExtension
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.buc.BUC
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.service.buc.KopierBucService
import no.nav.melosys.eessi.service.eux.EuxService
import no.nav.melosys.eessi.service.eux.OpprettBucOgSedResponse
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.NoSuchElementException

@ExtendWith(MockKExtension::class)
class KopierBucServiceTest {

    private val euxService: EuxService = mockk()
    private val saksrelasjonService: SaksrelasjonService = mockk()
    private val objectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())
    }
    private lateinit var kopierBucService: KopierBucService
    private lateinit var buc: BUC
    private lateinit var sedA001: SED

    @BeforeEach
    fun setup() {
        kopierBucService = KopierBucService(euxService, saksrelasjonService)
        buc = lesObjekt("mock/buc.json")
        sedA001 = lesObjekt("mock/sedA001.json")
    }

    @Test
    fun kopierBUC_laBuc01A001Finnes_oppretterNyBucMedYtterligereInfoOgOppdatererSaksrelasjon() {
        val rinaSaksnummer = "12423"
        val forventetNyRinasaksnummer = "222223"
        buc.documents.firstOrNull { it.type == SedType.A001.name }?.status = "SENT"
        every { euxService.hentBuc(rinaSaksnummer) } returns buc
        every { euxService.hentSed(eq(rinaSaksnummer), any()) } returns sedA001
        every {
            euxService.opprettBucOgSed(eq(BucType.LA_BUC_01), any(), eq(sedA001), any())
        } returns OpprettBucOgSedResponse(forventetNyRinasaksnummer, "")
        every { saksrelasjonService.finnVedRinaSaksnummer(rinaSaksnummer) } returns Optional.empty()

        kopierBucService.kopierBUC(rinaSaksnummer) shouldBe forventetNyRinasaksnummer
        sedA001
            .nav.shouldNotBeNull()
            .ytterligereinformasjon.shouldNotBeNull().apply {
                this shouldContain SedType.A001.name
                this shouldContain buc.internationalId!!
                length shouldBeLessThan 500 // Maks-lengde i RINA
            }
    }

    @Test
    fun kopierBUC_laBuc01A001FinnesIkke_kasterFeil() {
        val rinaSaksnummer = "12423"
        every { euxService.hentBuc(rinaSaksnummer) } returns buc

        val exception = shouldThrow<NoSuchElementException> {
            kopierBucService.kopierBUC(rinaSaksnummer)
        }
        exception.message shouldContain "Finner ikke første"
    }

    private inline fun <reified T> lesObjekt(fil: String): T {
        return objectMapper.readValue(javaClass.classLoader.getResource(fil), T::class.java)
    }
}
