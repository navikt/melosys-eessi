package no.nav.melosys.eessi.service.sed.helpers

import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.controller.dto.Bestemmelse
import no.nav.melosys.eessi.service.sed.helpers.A1GrunnlagMapper.mapFromBestemmelse
import org.junit.jupiter.api.Test

class A1GrunnlagMapperTest {

    @Test
    fun mapFromBestemmelse_expectString12_r() {
        mapFromBestemmelse(Bestemmelse.ART_12_1) shouldBe "12_r"
        mapFromBestemmelse(Bestemmelse.ART_12_2) shouldBe "12_r"
    }

    @Test
    fun mapFromBestemmelse_expectString16_R() {
        mapFromBestemmelse(Bestemmelse.ART_16_1) shouldBe "16_R"
        mapFromBestemmelse(Bestemmelse.ART_16_2) shouldBe "16_R"
    }

    @Test
    fun mapFromBestemmelse_expectStringAnnet() {
        mapFromBestemmelse(Bestemmelse.ART_11_1) shouldBe "annet"
        mapFromBestemmelse(Bestemmelse.ART_13_1_a) shouldBe "annet"
    }
}
