package no.nav.melosys.eessi.service.buc

import io.github.benas.randombeans.api.EnhancedRandom
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.melosys.eessi.EnhancedRandomCreator
import no.nav.melosys.eessi.controller.dto.SedStatus
import no.nav.melosys.eessi.metrikker.BucMetrikker
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.buc.Action
import no.nav.melosys.eessi.models.buc.BUC
import no.nav.melosys.eessi.models.buc.Conversation
import no.nav.melosys.eessi.models.buc.Document
import no.nav.melosys.eessi.models.bucinfo.BucInfo
import no.nav.melosys.eessi.models.exception.IntegrationException
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA001
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009
import no.nav.melosys.eessi.models.sed.nav.Nav
import no.nav.melosys.eessi.service.eux.BucSearch
import no.nav.melosys.eessi.service.eux.EuxService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.util.*

class LukkBucServiceTest {

    private val euxService: EuxService = mockk()
    private val bucMetrikker: BucMetrikker = mockk()
    private val enhancedRandom: EnhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom()
    private lateinit var lukkBucService: LukkBucService

    @BeforeEach
    fun setup() {
        lukkBucService = LukkBucService(euxService, bucMetrikker)
    }

    @Test
    fun lukkBucerAvType_enBucKanLukkes_verifiserOpprettOgSend() {
        val bucInfo = BucInfo(
            id = "123jfpw",
            applicationRoleId = "PO",
            status = "open"
        )

        val bucInfos = listOf(bucInfo)
        val buc = lagBuc()

        every { euxService.hentBucer(any<BucSearch>()) } returns bucInfos
        every { euxService.finnBUC(bucInfo.id) } returns Optional.of(buc)

        val sed = SED(
            nav = enhancedRandom.nextObject(Nav::class.java),
            medlemskap = enhancedRandom.nextObject(MedlemskapA009::class.java)
        )
        every { euxService.hentSed(any(), any()) } returns sed
        every { euxService.opprettOgSendSed(any(), any()) } returns Unit
        every { bucMetrikker.bucLukket(any()) } returns Unit

        lukkBucService.lukkBucerAvType(BucType.LA_BUC_04)

        verify { euxService.hentBucer(any<BucSearch>()) }
        verify { euxService.finnBUC(bucInfo.id) }
        verify { euxService.hentSed(buc.id, buc.documents[0].id) }
        verify { euxService.opprettOgSendSed(any<SED>(), buc.id!!) }
    }

    @Test
    fun lukkBucerAvType_enBucKanLukkesInneholderUtkastX001_verifiserOppdaterSåSend() {
        val bucInfo = BucInfo(
            id = "123jfpw",
            applicationRoleId = "PO",
            status = "open"
        )

        val bucInfos = listOf(bucInfo)
        val buc = lagBuc().copy(documents = lagBuc().documents.toMutableList().apply {
            add(
                Document(
                    type = SedType.X001.name,
                    creationDate = ZonedDateTime.now(),
                    conversations = emptyList(),
                    status = "draft"
                )
            )
        })

        every { euxService.hentBucer(any<BucSearch>()) } returns bucInfos
        every { euxService.finnBUC(bucInfo.id) } returns Optional.of(buc)

        val sed = SED(
            nav = enhancedRandom.nextObject(Nav::class.java),
            medlemskap = enhancedRandom.nextObject(MedlemskapA009::class.java)
        )
        every { euxService.hentSed(any(), any()) } returns sed
        every { euxService.oppdaterSed(any(), any(), any()) } returns Unit
        every { euxService.sendSed(any(), any(), any()) } returns Unit
        every { bucMetrikker.bucLukket(any()) } returns Unit

        lukkBucService.lukkBucerAvType(BucType.LA_BUC_04)

        verify { euxService.hentBucer(any<BucSearch>()) }
        verify { euxService.finnBUC(bucInfo.id) }
        verify { euxService.hentSed(buc.id, buc.documents[0].id) }
        verify { euxService.oppdaterSed(buc.id!!, buc.documents[1].id, any<SED>()) }
        verify { euxService.sendSed(buc.id, buc.documents[1].id, buc.documents[1].type) }
    }

    @Test
    fun lukkBucerAvType_feilVedHentingAvBucer_ingenVidereKall() {
        every { euxService.hentBucer(any<BucSearch>()) } throws IntegrationException("")
        lukkBucService.lukkBucerAvType(BucType.LA_BUC_04)
        verify(exactly = 0) { euxService.hentBuc(any()) }
    }

    @Test
    fun lukkBucerAvType_feilVedHentingAvBuc_ingenVidereKall() {
        val bucInfo = BucInfo(
            id = "123jfpw",
            applicationRoleId = "PO",
            status = "open"
        )

        val bucInfos = listOf(bucInfo)

        every { euxService.hentBucer(any<BucSearch>()) } returns bucInfos
        every { euxService.finnBUC(any<String>()) } returns Optional.empty()

        lukkBucService.lukkBucerAvType(BucType.LA_BUC_04)

        verify { euxService.finnBUC(any<String>()) }
        verify(exactly = 0) { euxService.hentSed(any(), any()) }
    }

    @Test
    fun lukkBucerAvType_feilVedHentingAvSed_ingenVidereKall() {
        val bucInfo = BucInfo(
            id = "123jfpw",
            applicationRoleId = "PO",
            status = "open"
        )

        val bucInfos = listOf(bucInfo)
        val buc = lagBuc()

        every { euxService.hentBucer(any<BucSearch>()) } returns bucInfos
        every { euxService.finnBUC(bucInfo.id) } returns Optional.of(buc)
        every { euxService.hentSed(any(), any()) } throws IntegrationException("")

        lukkBucService.lukkBucerAvType(BucType.LA_BUC_04)

        verify { euxService.hentBucer(any<BucSearch>()) }
        verify { euxService.finnBUC(bucInfo.id) }
        verify { euxService.hentSed(buc.id, buc.documents[0].id) }
        verify(exactly = 0) { euxService.opprettOgSendSed(any(), any()) }
    }

    @Test
    fun lukkBucerAvType_toDokumenter_brukSistOpprettetDokument() {
        val bucInfo = BucInfo(
            id = "123jfpw",
            applicationRoleId = "PO",
            status = "open"
        )

        val bucInfos = listOf(bucInfo)
        val buc = lagBuc()
        val sisteOppdatertDokumentId = buc.documents[0].id

        val document = Document(
            type = SedType.A008.name,
            creationDate = ZonedDateTime.now().minusDays(1L),
            status = "sent",
            id = "rrrr",
            conversations = emptyList()
        )
        val updatedBuc = buc.copy(documents = buc.documents.toMutableList().apply { add(document) })

        every { euxService.hentBucer(any<BucSearch>()) } returns bucInfos
        every { euxService.finnBUC(bucInfo.id) } returns Optional.of(updatedBuc)
        every { euxService.hentSed(any(), any()) } throws IntegrationException("")

        lukkBucService.lukkBucerAvType(BucType.LA_BUC_04)

        verify { euxService.hentBucer(any<BucSearch>()) }
        verify { euxService.finnBUC(bucInfo.id) }
        verify { euxService.hentSed(updatedBuc.id, sisteOppdatertDokumentId) }
        verify(exactly = 0) { euxService.opprettOgSendSed(any(), any()) }
    }

    @Test
    fun lukkBucerAvType_statusClosed_ingenBlirLukket() {
        val bucInfo = BucInfo(status = "closed")

        every { euxService.hentBucer(any()) } returns listOf(bucInfo)
        lukkBucService.lukkBucerAvType(BucType.LA_BUC_02)
        verify(exactly = 0) { euxService.hentBuc(any()) }
    }

    @Test
    fun lukkBucerAvType_LABUC06ToMndSidenMottattA006_lukkes() {
        val bucInfo = BucInfo(
            id = "123jfpw",
            applicationRoleId = "PO",
            status = "open"
        )

        val buc = lagBuc(BucType.LA_BUC_06, SedType.A005)
        val sisteOppdatertDokumentId = buc.documents[0].id

        val document = Document(
            type = SedType.A006.name,
            lastUpdate = ZonedDateTime.now().minusMonths(2),
            status = SedStatus.MOTTATT.engelskStatus,
            id = "mottattA006-123",
            conversations = emptyList()
        )
        val updatedBuc = buc.copy(documents = buc.documents.toMutableList().apply { add(document) })

        every { euxService.hentBucer(any<BucSearch>()) } returns listOf(bucInfo)
        every { euxService.finnBUC(bucInfo.id) } returns Optional.of(updatedBuc)
        every { euxService.opprettOgSendSed(any(), any()) } returns Unit
        every { bucMetrikker.bucLukket(any()) } returns Unit

        val sed = SED(
            nav = enhancedRandom.nextObject(Nav::class.java),
            medlemskap = enhancedRandom.nextObject(MedlemskapA009::class.java)
        )
        every { euxService.hentSed(any(), any()) } returns sed

        lukkBucService.lukkBucerAvType(BucType.LA_BUC_06)

        verify { euxService.hentBucer(any<BucSearch>()) }
        verify { euxService.finnBUC(bucInfo.id) }
        verify { euxService.hentSed(updatedBuc.id, sisteOppdatertDokumentId) }
        verify { euxService.opprettOgSendSed(any<SED>(), updatedBuc.id) }
    }

    @Test
    fun lukkBucerAvType_LABUC06A006IkkeMottatt_lukkesIkke() {
        val bucInfo = BucInfo(
            id = "123jfpw",
            applicationRoleId = "PO",
            status = "open"
        )

        val buc = lagBuc(BucType.LA_BUC_06, SedType.A005)
        val sisteOppdatertDokumentId = buc.documents[0].id

        every { euxService.hentBucer(any<BucSearch>()) } returns listOf(bucInfo)
        every { euxService.finnBUC(bucInfo.id) } returns Optional.of(buc)

        lukkBucService.lukkBucerAvType(BucType.LA_BUC_06)

        verify { euxService.hentBucer(any<BucSearch>()) }
        verify { euxService.finnBUC(bucInfo.id) }
        verify(exactly = 0) { euxService.hentSed(buc.id, sisteOppdatertDokumentId) }
        verify(exactly = 0) { euxService.opprettOgSendSed(any(), buc.id!!) }
    }

    @Test
    fun lukkBucerAvType_LABUC01A001IkkeMottattSvarPåA001_lukkesIkke() {
        val bucInfo = BucInfo(
            id = "123jfpw",
            applicationRoleId = "PO",
            status = "open"
        )

        val buc = lagBuc(BucType.LA_BUC_01, SedType.A001)

        every { euxService.hentBucer(any<BucSearch>()) } returns listOf(bucInfo)
        every { euxService.finnBUC(bucInfo.id) } returns Optional.of(buc)

        lukkBucService.lukkBucerAvType(BucType.LA_BUC_01)

        verify { euxService.hentBucer(any<BucSearch>()) }
        verify { euxService.finnBUC(bucInfo.id) }
        verify(exactly = 0) { euxService.hentSed(any(), any()) }
        verify(exactly = 0) { euxService.opprettOgSendSed(any(), buc.id!!) }
    }

    @Test
    fun lukkBucerAvType_LABUC01A001MottattA011For20DagerSiden_lukkesIkke() {
        val bucInfo = BucInfo(
            id = "123jfpw",
            applicationRoleId = "PO",
            status = "open"
        )

        val buc = lagBuc(BucType.LA_BUC_01, SedType.A001)

        val a011 = Document(
            type = SedType.A011.name,
            direction = "IN",
            lastUpdate = ZonedDateTime.now().minusDays(20)
        )
        val updatedBuc = buc.copy(documents = buc.documents.toMutableList().apply { add(a011) })

        every { euxService.hentBucer(any<BucSearch>()) } returns listOf(bucInfo)
        every { euxService.finnBUC(bucInfo.id) } returns Optional.of(updatedBuc)

        lukkBucService.lukkBucerAvType(BucType.LA_BUC_01)

        verify { euxService.hentBucer(any<BucSearch>()) }
        verify { euxService.finnBUC(bucInfo.id) }
        verify(exactly = 0) { euxService.hentSed(any(), any()) }
        verify(exactly = 0) { euxService.opprettOgSendSed(any(), updatedBuc.id) }
    }

    @Test
    fun lukkBucerAvType_LABUC01A001MottattA011Mottatt80DagerSiden_lukkes() {
        val bucInfo = BucInfo(
            id = "123jfpw",
            applicationRoleId = "PO",
            status = "open"
        )

        val buc = lagBuc(BucType.LA_BUC_01, SedType.A001).copy(
            documents = mutableListOf(
                Document(
                    type = SedType.A001.name,
                    lastUpdate = ZonedDateTime.now().minusDays(100),
                    status = SedStatus.SENDT.engelskStatus,
                    id = "a001-123",
                    conversations = emptyList()
                )
            )
        )

        val a011 = Document(
            type = SedType.A011.name,
            direction = "IN",
            lastUpdate = ZonedDateTime.now().minusDays(80),
            status = SedStatus.MOTTATT.engelskStatus,
            conversations = listOf(Conversation(versionId = "1"))

        )
        val updatedBuc = buc.copy(documents = buc.documents.toMutableList().apply { add(a011) })

        val sed = SED(
            nav = enhancedRandom.nextObject(Nav::class.java),
            medlemskap = enhancedRandom.nextObject(MedlemskapA001::class.java)
        )
        every { euxService.hentSed(any(), any()) } returns sed

        every { euxService.hentBucer(any<BucSearch>()) } returns listOf(bucInfo)
        every { euxService.finnBUC(bucInfo.id) } returns Optional.of(updatedBuc)
        every { euxService.opprettOgSendSed(any(), any()) } returns Unit
        every { bucMetrikker.bucLukket(any()) } returns Unit

        lukkBucService.lukkBucerAvType(BucType.LA_BUC_01)

        verify { euxService.hentBucer(any<BucSearch>()) }
        verify { euxService.finnBUC(bucInfo.id) }
        verify { euxService.opprettOgSendSed(any(), updatedBuc.id) }
    }

    @Test
    fun lukkBucerAvType_LaBuc02kanBareOppretteA012_lukkesIkke() {
        val bucInfo = BucInfo(
            id = "123jfpw",
            applicationRoleId = "PO",
            status = "open"
        )

        val buc = lagBuc(BucType.LA_BUC_02, SedType.A003).copy(
            actions = mutableListOf(
                Action(
                    documentType = SedType.A012.name,
                    operation = "create"
                )
            )
        )

        every { euxService.hentBucer(any<BucSearch>()) } returns listOf(bucInfo)
        every { euxService.finnBUC(bucInfo.id) } returns Optional.of(buc)

        lukkBucService.lukkBucerAvType(BucType.LA_BUC_01)

        verify { euxService.hentBucer(any<BucSearch>()) }
        verify { euxService.finnBUC(bucInfo.id) }
        verify(exactly = 0) { euxService.hentSed(any(), any()) }
        verify(exactly = 0) { euxService.opprettOgSendSed(any(), buc.id!!) }
    }

    @Test
    fun lukkBucerAvType_LABUC03A008Sendt30DagerSiden_lukkes() {
        val bucInfo = BucInfo(
            id = "123jfpw",
            applicationRoleId = "PO",
            status = "open"
        )

        val buc = lagBuc(BucType.LA_BUC_03, SedType.A001).copy(
            documents = mutableListOf(
                Document(
                    type = SedType.A001.name,
                    lastUpdate = ZonedDateTime.now().minusDays(40),
                    status = SedStatus.SENDT.engelskStatus,
                    id = "a001-123",
                    conversations = emptyList()
                )
            )
        )

        val a008 = Document(
            type = SedType.A008.name,
            direction = "IN",
            lastUpdate = ZonedDateTime.now().minusDays(30),
            status = SedStatus.SENDT.engelskStatus,
            conversations = listOf(Conversation(versionId = "1"))
        )
        val updatedBuc = buc.copy(documents = buc.documents + a008)

        val sed = SED(
            nav = enhancedRandom.nextObject(Nav::class.java),
            medlemskap = enhancedRandom.nextObject(MedlemskapA001::class.java)
        )
        every { euxService.hentSed(any(), any()) } returns sed
        every { euxService.hentBucer(any<BucSearch>()) } returns listOf(bucInfo)
        every { euxService.finnBUC(bucInfo.id) } returns Optional.of(updatedBuc)
        every { euxService.opprettOgSendSed(any(), any()) } returns Unit
        every { bucMetrikker.bucLukket(any()) } returns Unit

        lukkBucService.lukkBucerAvType(BucType.LA_BUC_03)

        verify { euxService.hentBucer(any<BucSearch>()) }
        verify { euxService.finnBUC(bucInfo.id) }
        verify { euxService.opprettOgSendSed(any(), updatedBuc.id) }
    }

    private fun lagBuc() = lagBuc(BucType.LA_BUC_04, SedType.A009)

    private fun lagBuc(bucType: BucType, sedType: SedType): BUC {
        return BUC(
            id = "ffff",
            bucType = bucType.name,
            actions = mutableListOf(
                Action(
                    documentType = SedType.X001.name,
                    operation = "create"
                )
            ),
            documents = mutableListOf(
                Document(
                    type = sedType.name,
                    creationDate = ZonedDateTime.now(),
                    status = "sent",
                    conversations = listOf(Conversation(versionId = "1")),
                    id = "gjrieogroei"
                )
            ),
            bucVersjon = "4.1"
        )
    }
}
