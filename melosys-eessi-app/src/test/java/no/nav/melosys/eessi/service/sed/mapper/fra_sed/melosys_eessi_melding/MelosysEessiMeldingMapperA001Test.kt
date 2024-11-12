package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA001
import no.nav.melosys.eessi.models.sed.nav.Fastperiode
import no.nav.melosys.eessi.models.sed.nav.Grunnlag
import no.nav.melosys.eessi.models.sed.nav.Land
import no.nav.melosys.eessi.models.sed.nav.Unntak
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSed
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSedHendelse
import org.junit.jupiter.api.Test

class MelosysEessiMeldingMapperA001Test {

    private val melosysEessiMeldingMapperFactory = MelosysEessiMeldingMapperFactory("dummy")

    @Test
    fun mapA001_forventRettFelt() {
        val sed = createSed(hentMedlemskap())
        val sedHendelse = createSedHendelse()

        val melosysEessiMelding = melosysEessiMeldingMapperFactory.getMapper(SedType.A001).map(
            EessiMeldingQuery(
                aktoerId = "123",
                sed = sed,
                rinaDokumentID = sedHendelse.rinaDokumentId,
                rinaSaksnummer = sedHendelse.rinaSakId,
                sedType = sedHendelse.sedType,
                bucType = sedHendelse.bucType,
                avsenderID = sedHendelse.avsenderId,
                landkode = "landkode",
                sedErEndring = false,
                sedVersjon = "1"
            )
        )

        melosysEessiMelding.shouldNotBeNull().run {
            artikkel shouldBe "16_1"
            lovvalgsland shouldBe "NO"
            anmodningUnntak shouldNotBe null
            anmodningUnntak?.unntakFraLovvalgsbestemmelse shouldBe "12_1"
            anmodningUnntak?.unntakFraLovvalgsland shouldBe "SE"
        }
    }

    private fun hentMedlemskap(): MedlemskapA001 = MedlemskapA001().apply {
        soeknadsperiode = Fastperiode().apply {
            sluttdato = "2019-12-01"
            startdato = "2019-05-01"
        }
        naavaerendemedlemskap = mutableListOf(Land().apply { landkode = "SE" })
        forespurtmedlemskap = mutableListOf(Land().apply { landkode = "NO" })
        unntak = Unntak().apply {
            grunnlag = Grunnlag().apply { artikkel = "12_1" }
        }
    }
}
