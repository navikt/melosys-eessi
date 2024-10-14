package no.nav.melosys.eessi.service.helpers

import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper
import org.junit.jupiter.api.Test

class LandkodeMapperTest {

    @Test
    fun `getIso2 expect Iso2`() {
        LandkodeMapper.mapTilLandkodeIso2("NOR") shouldBe "NO"
        LandkodeMapper.mapTilLandkodeIso2("SWE") shouldBe "SE"
        LandkodeMapper.mapTilLandkodeIso2("DNK") shouldBe "DK"
    }

    @Test
    fun `getIso2 with Iso2 expect Iso2`() {
        LandkodeMapper.mapTilLandkodeIso2("NO") shouldBe "NO"
        LandkodeMapper.mapTilLandkodeIso2("SE") shouldBe "SE"
        LandkodeMapper.mapTilLandkodeIso2("DK") shouldBe "DK"
    }

    @Test
    fun `getIso2 med ikke ISO standard koder forvent samme kode tilbake`() {
        LandkodeMapper.mapTilLandkodeIso2("???") shouldBe "XU"
        LandkodeMapper.mapTilLandkodeIso2("XXX") shouldBe "XS"
        LandkodeMapper.mapTilLandkodeIso2("XUK") shouldBe "XU"
    }

    @Test
    fun `skal returnere ISO3 kode for gyldig ISO2 kode`() {
        LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("US", true) shouldBe "USA"
    }

    @Test
    fun `finn Landkode Iso3 for ident rekvisisjon ikke funnet gir ukjent`() {
        LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("AB", false) shouldBe "XUK"
    }

    @Test
    fun `finn Landkode Iso3 for ident rekvisisjon ikke funnet gir null`() {
        LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("AB", true) shouldBe null
    }

    @Test
    fun `skal returnere samme kode for ISO3 kode`() {
        LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("USA", true) shouldBe "USA"
    }

    @Test
    fun `skal returnere ukjent for ugyldig ISO2 kode felleskodeverk format`() {
        LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("ZZ", false) shouldBe "XUK"
    }

    @Test
    fun `skal returnere ukjent for ugyldig ISO2 kode PDL format`() {
        LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("ZZ", true) shouldBe null
    }

    @Test
    fun `skal returnere null for null inndata`() {
        LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon(null, true) shouldBe null
    }

    @Test
    fun `skal returnere ukjent for tom streng med felleskodeverk format`() {
        LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("", false) shouldBe "XUK"
    }

    @Test
    fun `skal returnere ukjent for tom streng med PDL format`() {
        LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("", false) shouldBe "XUK"
    }

    @Test
    fun `skal returnere GB sin ISO3 for UK`() {
        LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("UK", false) shouldBe "GBR"
    }

    @Test
    fun `skal returnere GR sin ISO3 i for EL`() {
        LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("EL", false) shouldBe "GRC"
    }
}
