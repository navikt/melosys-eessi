package no.nav.melosys.eessi.models.buc

import io.kotest.matchers.collections.shouldContain
import no.nav.melosys.eessi.controller.dto.SedStatus
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.SedType
import java.time.ZonedDateTime
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class BUCTest {

    @Test
    fun hentSistOppdaterteDocument() {
        val document1 = Document(
            status = SedStatus.MOTTATT.engelskStatus,
            lastUpdate = ZonedDateTime.now()
        )
        val document2 = Document(
            status = SedStatus.SENDT.engelskStatus,
            lastUpdate = ZonedDateTime.now().plusWeeks(1)
        )

        val buc = BUC(
            documents = listOf(document1, document2)
        )

        buc.hentSistOppdaterteDocument() shouldBe document2
    }

    @Test
    fun kanLukkesAutomatisk_LABUC06_30dagerSidenA006() {
        val buc06 = BUC(
            bucType = BucType.LA_BUC_06.name,
            actions = listOf(Action("name", SedType.X001.name, "1", "UPDATE")),
            documents = listOf(createDocument(SedType.A006, SedStatus.MOTTATT, 30, ""))
        )

        buc06.kanLukkesAutomatisk() shouldBe true
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC06_30dagerSidenA006OperationREAD() {
        val buc06 = BUC(
            bucType = BucType.LA_BUC_06.name,
            actions = listOf(Action("name", SedType.X001.name, "1", "READ")),
            documents = listOf(createDocument(SedType.A006, SedStatus.MOTTATT, 30, ""))
        )

        buc06.kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanLukkesAutomatisk_LABUC01_60dagerSidenA011A002() {
        val buc01 = BUC(
            bucType = BucType.LA_BUC_01.name,
            actions = listOf(Action("name", SedType.X001.name, "1", "UPDATE")),
            documents = listOf(
                createDocument(SedType.A002, SedStatus.MOTTATT, 60, "IN"),
                createDocument(SedType.A011, SedStatus.MOTTATT, 60, "IN")
            )
        )

        buc01.kanLukkesAutomatisk() shouldBe true
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC01_59dagerSidenA011A002() {
        val buc01 = BUC(
            bucType = BucType.LA_BUC_01.name,
            actions = listOf(Action("name", SedType.X001.name, "1", "UPDATE")),
            documents = listOf(
                createDocument(SedType.A002, SedStatus.MOTTATT, 59, "IN"),
            )
        )

        buc01.kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC01_StatusTOM() {
        val buc01 = BUC(
            bucType = BucType.LA_BUC_01.name,
            actions = listOf(Action("name", SedType.X001.name, "1", "UPDATE")),
            documents = listOf(
                createDocument(SedType.A002, SedStatus.TOM, 60, "IN"),
            )
        )

        buc01.kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC03_20dagerSidenX013() {
        val buc03 = BUC(
            bucType = BucType.LA_BUC_03.name,
            actions = listOf(Action("name", SedType.X001.name, "1", "UPDATE")),
            documents = listOf(
                createDocument(SedType.X012, SedStatus.MOTTATT, 30, ""),
                createDocument(SedType.X013, SedStatus.SENDT, 20, ""),
                createDocument(SedType.A008, SedStatus.SENDT, 30, "IN")
            )
        )

        buc03.kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC03_20dagerSidenX012() {
        val buc03 = BUC(
            bucType = BucType.LA_BUC_03.name,
            actions = listOf(Action("name", SedType.X001.name, "1", "UPDATE")),
            documents = listOf(
                createDocument(SedType.X012, SedStatus.MOTTATT, 20, ""),
                createDocument(SedType.X013, SedStatus.SENDT, 30, ""),
                createDocument(SedType.A008, SedStatus.SENDT, 30, "IN")
            )
        )

        buc03.kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC03_30dagerSidenX013og25dagerSidenA008() {
        val buc03 = BUC(
            bucType = BucType.LA_BUC_03.name,
            actions = listOf(Action("name", SedType.X001.name, "1", "UPDATE")),
            documents = listOf(
                createDocument(SedType.X012, SedStatus.MOTTATT, 20, ""),
                createDocument(SedType.X013, SedStatus.SENDT, 30, ""),
                createDocument(SedType.A008, SedStatus.SENDT, 25, "IN")
            )
        )

        buc03.kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC03_30dagerSidenA008ogX01300dagerSidenX012() {
        val buc03 = BUC(
            bucType = BucType.LA_BUC_03.name,
            actions = listOf(Action("name", SedType.X001.name, "1", "UPDATE")),
            documents = listOf(
                createDocument(SedType.X012, SedStatus.MOTTATT, 10, ""),
                createDocument(SedType.X013, SedStatus.SENDT, 30, ""),
                createDocument(SedType.A008, SedStatus.SENDT, 30, "IN")
            )
        )

        buc03.kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC03_30dagerSidenA008ogX01210dagerSidenX013() {
        val buc03 = BUC(
            bucType = BucType.LA_BUC_03.name,
            actions = listOf(Action("name", SedType.X001.name, "1", "UPDATE")),
            documents = listOf(
                createDocument(SedType.X012, SedStatus.MOTTATT, 30, ""),
                createDocument(SedType.X013, SedStatus.SENDT, 10, ""),
                createDocument(SedType.A008, SedStatus.SENDT, 30, "IN")
            )
        )

        buc03.kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanLukkesAutomatisk_LABUC03_30dagerSidenA008ogX012() {
        val buc03 = BUC(
            bucType = BucType.LA_BUC_03.name,
            actions = listOf(Action("name", SedType.X001.name, "1", "UPDATE")),
            documents = listOf(
                createDocument(SedType.X012, SedStatus.MOTTATT, 30, ""),
                createDocument(SedType.A008, SedStatus.SENDT, 30, "IN")
            )
        )

        buc03.kanLukkesAutomatisk() shouldBe true
    }

    @Test
    fun kanLukkesAutomatisk_LABUC03_30dagerSidenA008ogX013() {
        val buc03 = BUC(
            bucType = BucType.LA_BUC_03.name,
            actions = listOf(Action("name", SedType.X001.name, "1", "UPDATE")),
            documents = listOf(
                createDocument(SedType.X013, SedStatus.MOTTATT, 30, ""),
                createDocument(SedType.A008, SedStatus.SENDT, 30, "IN")
            )
        )

        buc03.kanLukkesAutomatisk() shouldBe true
    }

    @Test
    fun kanLukkesAutomatisk_LABUC03_30dagerSidenA008() {
        val buc03 = BUC(
            bucType = BucType.LA_BUC_03.name,
            actions = listOf(Action("name", SedType.X001.name, "1", "UPDATE")),
            documents = listOf(
                createDocument(SedType.A008, SedStatus.SENDT, 30, "IN")
            )
        )

        buc03.kanLukkesAutomatisk() shouldBe true
    }

    @Test
    fun hentAvsenderLand() {
        val buc = BUC(creator = Creator(organisation = Organisation(countryCode = "NO")))

        buc.hentAvsenderLand() shouldBe "NO"
    }

    @Test
    fun kanOppretteEllerOppdatereSed() {
        val buc = BUC(actions = listOf(Action(documentType = SedType.X001.name, operation = "UPDATE")))

        buc.kanOppretteEllerOppdatereSed(SedType.X001) shouldBe true
    }

    @Test
    fun hentDokument() {
        val buc = BUC(documents = listOf(Document(id = "123")))

        buc.hentDokument("123") shouldBe Document(id = "123")
    }

    @Test
    fun hentDokument_notFound() {
        val buc = BUC(documents = listOf())

        assertFailsWith<NoSuchElementException> {
            buc.hentDokument("123")
        }
    }

    @Test
    fun erÅpen() {
        val buc = BUC(status = "open")

        buc.erÅpen() shouldBe true
    }

    @Test
    fun erÅpen_closed() {
        val buc = BUC(status = "closed")

        buc.erÅpen() shouldBe false
    }

    @Test
    fun finnDokumentVedSedType() {
        val buc = BUC(documents = listOf(Document(type = SedType.X001.name)))

        buc.finnDokumentVedSedType(SedType.X001.name) shouldBe Document(type = SedType.X001.name)
    }

    @Test
    fun sedKanOppdateres() {
        val buc = BUC(actions = listOf(Action(documentId = "123", operation = "UPDATE")))

        buc.sedKanOppdateres("123") shouldBe true
    }

    @Test
    fun finnFørstMottatteSed() {
        val buc = BUC(
            documents = listOf(
                Document(
                    type = SedType.X001.name,
                    creationDate = ZonedDateTime.now().minusDays(1),
                    direction = "IN",
                    status = SedStatus.MOTTATT.engelskStatus
                )
            )
        )

        buc.finnFørstMottatteSed().isPresent shouldBe true
    }

    @Test
    fun hentMottakere() {
        val buc = BUC(
            participants = listOf(
                Participant(
                    organisation = Organisation(id = "ORG1"),
                    role = Participant.ParticipantRole.MOTPART
                )
            )
        )

        buc.hentMottakere() shouldContain "ORG1"
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC02_A003SendtForEnDagSiden() {
        laBuc02(sendtA003(1)).kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanLukkesAutomatisk_LABUC02_A003Sendt61DagerSidenIngenMottatt() {
        laBuc02(sendtA003(61)).kanLukkesAutomatisk() shouldBe true
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC02_A003Sendt61DagerSidenUtenX001Action() {
        laBuc02(sendtA003(61), kanOppretteX001 = false).kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC02_IngenSendtA003() {
        laBuc02(mottatt(SedType.A012, 10)).kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC02_A003MedStatusTom() {
        val a003Tom = createDocument(SedType.A003, SedStatus.TOM, 61, "OUT")

        laBuc02(a003Tom).kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanLukkesAutomatisk_LABUC02_BareTillatteMottatt_SisteErIkkeA012() {
        laBuc02(
            sendtA003(61),
            mottatt(SedType.X009, 30),
            mottatt(SedType.A012, 20),
            mottatt(SedType.X007, 10)
        ).kanLukkesAutomatisk() shouldBe true
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC02_MottattA004() {
        laBuc02(sendtA003(61), mottatt(SedType.A004, 10)).kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanLukkesAutomatisk_LABUC02_MottattA004_SisteErA012() {
        laBuc02(
            sendtA003(61),
            mottatt(SedType.A004, 20),
            mottatt(SedType.A012, 10)
        ).kanLukkesAutomatisk() shouldBe true
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC02_MottattA004_A012_SisteErX009() {
        laBuc02(
            sendtA003(61),
            mottatt(SedType.A004, 30),
            mottatt(SedType.A012, 20),
            mottatt(SedType.X009, 10)
        ).kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC02_MottattA012_SisteErA004() {
        laBuc02(
            sendtA003(61),
            mottatt(SedType.A012, 20),
            mottatt(SedType.A004, 10)
        ).kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanLukkesAutomatisk_LABUC02_UkjentSed_SisteErA012() {
        laBuc02(
            sendtA003(61),
            mottatt(SedType.X010, 20),
            mottatt(SedType.A012, 10)
        ).kanLukkesAutomatisk() shouldBe true
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC02_A003Sendt30DagerSiden_MottattA012() {
        laBuc02(sendtA003(30), mottatt(SedType.A012, 1)).kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanLukkesAutomatisk_LABUC02_A003Sendt61DagerSiden_A012MottattIGår() {
        laBuc02(sendtA003(61), mottatt(SedType.A012, 1)).kanLukkesAutomatisk() shouldBe true
    }

    @Test
    fun kanIkkeLukkesAutomatisk_LABUC02_FørsteA003IListenSendtForTiDagerSiden() {
        laBuc02(
            sendtA003(10),
            mottatt(SedType.A004, 100),
            sendtA003(120)
        ).kanLukkesAutomatisk() shouldBe false
    }

    @Test
    fun kanLukkesAutomatisk_LABUC02_MottattX100Ignoreres() {
        laBuc02(sendtA003(61), mottatt(SedType.X100, 10)).kanLukkesAutomatisk() shouldBe true
    }

    @Test
    fun kanLukkesAutomatisk_LABUC02_MottattSedMedStatusTomIgnoreres() {
        laBuc02(
            sendtA003(61),
            createDocument(SedType.A004, SedStatus.TOM, 10, "IN")
        ).kanLukkesAutomatisk() shouldBe true
    }

    private fun laBuc02(vararg documents: Document, kanOppretteX001: Boolean = true) = BUC(
        bucType = BucType.LA_BUC_02.name,
        actions = if (kanOppretteX001) listOf(Action("name", SedType.X001.name, "1", "CREATE")) else listOf(),
        documents = documents.toList()
    )

    private fun sendtA003(dagerSiden: Long) = createDocument(SedType.A003, SedStatus.SENDT, dagerSiden, "OUT")

    private fun mottatt(sedType: SedType, dagerSiden: Long) = createDocument(sedType, SedStatus.MOTTATT, dagerSiden, "IN")

    private fun createDocument(sedType: SedType, sedStatus: SedStatus, days: Long, direction: String) = Document(
        type = sedType.name,
        status = sedStatus.engelskStatus,
        lastUpdate = ZonedDateTime.now().minusDays(days).minusSeconds(1),
        direction = direction
    )
}
