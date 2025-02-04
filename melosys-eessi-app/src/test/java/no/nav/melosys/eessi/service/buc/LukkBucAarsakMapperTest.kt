package no.nav.melosys.eessi.service.buc

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.buc.BUC
import no.nav.melosys.eessi.models.buc.Document
import org.junit.jupiter.api.Test

class LukkBucAarsakMapperTest {

    @Test
    fun `hentAarsakForLukking LABUC01 validerTekst`() {
        val buc = BUC(bucType = BucType.LA_BUC_01.name)

        val aarsakForLukking = LukkBucAarsakMapper.hentAarsakForLukking(buc)

        aarsakForLukking shouldContain "oppnådd_enighet"
    }

    @Test
    fun `hentAarsakForLukking LABUC02InneholderA012 validerTekst`() {
        val document = Document().apply {
            type = SedType.A012.name
        }
        val buc = BUC(bucType = BucType.LA_BUC_02.name, documents = listOf(document))

        val aarsak = LukkBucAarsakMapper.hentAarsakForLukking(buc)

        aarsak shouldBe "gjeldende_lovgivning_det_ble_oppnådd_enighet_om_anmodningen_om_unntak"
    }

    @Test
    fun `hentAarsakForLukking LABUC02UtenA012 validerTekst`() {
        val buc = BUC(bucType = BucType.LA_BUC_02.name)

        val aarsak = LukkBucAarsakMapper.hentAarsakForLukking(buc)

        aarsak shouldBe "gjeldende_lovgivning_fastsettelsen_ble_endelig_ingen_reaksjon_innen_2_måneder"
    }

    @Test
    fun `hentAarsakForLukking LABUC03 validerTekst`() {
        val buc = BUC(bucType = BucType.LA_BUC_03.name)

        val aarsak = LukkBucAarsakMapper.hentAarsakForLukking(buc)

        aarsak shouldBe "lovvalg_30_dager_siden_melding_om_relevant_informasjon"
    }

    @Test
    fun `hentAarsakForLukking LABUC04 validerTekst`() {
        val buc = BUC(bucType = BucType.LA_BUC_04.name)

        val aarsak = LukkBucAarsakMapper.hentAarsakForLukking(buc)

        aarsak shouldBe "lovvalg_30_dager_siden_melding_om_utstasjonering"
    }

    @Test
    fun `hentAarsakForLukking LABUC05 validerTekst`() {
        val buc = BUC(bucType = BucType.LA_BUC_05.name)

        val aarsak = LukkBucAarsakMapper.hentAarsakForLukking(buc)

        aarsak shouldBe "lovvalg_30_dager_siden_melding_om_utstasjonering"
    }

    @Test
    fun `hentAarsakForLukking LABUC06 validerTekst`() {
        val buc = BUC(bucType = BucType.LA_BUC_06.name)

        val aarsak = LukkBucAarsakMapper.hentAarsakForLukking(buc)

        aarsak shouldBe "gjeldende_lovgivning_30_dager_siden_svar_på_anmodning_om_mer_informasjon"
    }
}
