package no.nav.melosys.eessi.models.buc

import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.models.sed.Konstanter
import no.nav.melosys.eessi.models.sed.SED
import org.junit.jupiter.api.Test


class SedVersjonSjekkerTest {

    @Test
    fun `verifiserSedVersjonErBucVersjon - er lik versjon, oppdateres ikke`() {
        val buc = BUC(bucVersjon = "v4.1")
        val sed = SED(sedGVer = "4", sedVer = "1")

        SedVersjonSjekker.verifiserSedVersjonErBucVersjon(buc, sed)
        sed.sedGVer shouldBe "4"
        sed.sedVer shouldBe "1"
    }

    @Test
    fun `verifiserSedVersjonErBucVersjon - er forskjellig versjon, oppdateres`() {
        val buc = BUC(bucVersjon = "v5.4")
        val sed = SED(sedGVer = "4", sedVer = "1")

        SedVersjonSjekker.verifiserSedVersjonErBucVersjon(buc, sed)
        sed.sedGVer shouldBe "5"
        sed.sedVer shouldBe "4"
    }

    @Test
    fun `hentBucVersjon - riktig format, fungerer`() {
        val buc = BUC(bucVersjon = "v4.0")

        SedVersjonSjekker.parseGVer(buc) shouldBe "4"
        SedVersjonSjekker.parseVer(buc) shouldBe "0"
    }

    @Test
    fun `hentBucVersjon - uventet format, får default`() {
        val buc = BUC(bucVersjon = "v21")

        SedVersjonSjekker.parseGVer(buc) shouldBe Konstanter.DEFAULT_SED_G_VER
        SedVersjonSjekker.parseVer(buc) shouldBe Konstanter.DEFAULT_SED_VER
    }
}
