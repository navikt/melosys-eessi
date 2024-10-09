package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA001
import no.nav.melosys.eessi.models.sed.nav.Fastperiode
import no.nav.melosys.eessi.models.sed.nav.Grunnlag
import no.nav.melosys.eessi.models.sed.nav.Land
import no.nav.melosys.eessi.models.sed.nav.Unntak
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSed
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSedHendelse
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class MelosysEessiMeldingMapperA001Test {
    private val melosysEessiMeldingMapperFactory = MelosysEessiMeldingMapperFactory("dummy")

    @Test
    fun mapA001_forventRettFelt() {
        val sed = createSed(hentMedlemskap())
        val sedHendelse = createSedHendelse()
        val melosysEessiMelding = melosysEessiMeldingMapperFactory.getMapper(SedType.A001)
            .map(
                "123",
                sed,
                sedHendelse.rinaDokumentId,
                sedHendelse.rinaSakId,
                sedHendelse.sedType,
                sedHendelse.bucType,
                sedHendelse.avsenderId,
                "landkode",
                null,
                null,
                null,
                false,
                "1"
            )

        Assertions.assertThat(melosysEessiMelding).isNotNull()
        Assertions.assertThat(melosysEessiMelding.artikkel).isEqualTo("16_1")
        Assertions.assertThat(melosysEessiMelding.lovvalgsland).isEqualTo("NO")
        Assertions.assertThat(melosysEessiMelding.anmodningUnntak).isNotNull()
        Assertions.assertThat(melosysEessiMelding.anmodningUnntak.unntakFraLovvalgsbestemmelse).isEqualTo("12_1")
        Assertions.assertThat(melosysEessiMelding.anmodningUnntak.unntakFraLovvalgsland).isEqualTo("SE")
    }

    private fun hentMedlemskap(): MedlemskapA001 {
        val medlemskap = MedlemskapA001()

        val fastperiode = Fastperiode()
        fastperiode.sluttdato = "2019-12-01"
        fastperiode.startdato = "2019-05-01"
        medlemskap.soeknadsperiode = fastperiode

        val sverige = Land()
        sverige.landkode = "SE"
        medlemskap.naavaerendemedlemskap = mutableListOf(sverige)

        val norge = Land()
        norge.landkode = "NO"
        medlemskap.forespurtmedlemskap = mutableListOf(norge)

        val unntak = Unntak()
        val grunnlag = Grunnlag()
        grunnlag.artikkel = "12_1"
        unntak.grunnlag = grunnlag
        medlemskap.unntak = unntak

        return medlemskap
    }
}
