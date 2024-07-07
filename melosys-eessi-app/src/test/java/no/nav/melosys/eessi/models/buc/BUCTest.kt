package no.nav.melosys.eessi.models.buc

import no.nav.melosys.eessi.controller.dto.SedStatus
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.SedType
import java.time.ZonedDateTime
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

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

    private fun createDocument(sedType: SedType, sedStatus: SedStatus, days: Long, direction: String): Document {
        return Document(
            type = sedType.name,
            status = sedStatus.engelskStatus,
            lastUpdate = ZonedDateTime.now().minusDays(days).minusSeconds(1),
            direction = direction
        )
    }
}
