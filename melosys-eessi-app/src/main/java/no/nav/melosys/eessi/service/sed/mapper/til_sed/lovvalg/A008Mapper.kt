package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.Periode
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA008
import no.nav.melosys.eessi.models.sed.nav.ArbeidIFlereLand
import no.nav.melosys.eessi.models.sed.nav.Bosted
import no.nav.melosys.eessi.models.sed.nav.MedlemskapA008Bruker
import no.nav.melosys.eessi.models.sed.nav.Yrkesaktivitet
import java.time.LocalDate
import java.util.*

class A008Mapper : LovvalgSedMapper<MedlemskapA008> {
    override fun getMedlemskap(sedData: SedDataDto): MedlemskapA008 {
        return MedlemskapA008(null, hentA008Bruker(sedData))
    }

    private fun hentA008Bruker(sedData: SedDataDto): MedlemskapA008Bruker {
        val bruker = MedlemskapA008Bruker()
        val arbeidIFlereLand = ArbeidIFlereLand()
        arbeidIFlereLand.bosted = Bosted(sedData.avklartBostedsland)

        Optional.ofNullable(sedData.søknadsperiode)
            .map(Periode::fom)
            .ifPresent { søknadsperiodeFom: LocalDate? ->
                val yrkesaktivitet = Yrkesaktivitet()
                yrkesaktivitet.startdato = formaterDato(søknadsperiodeFom!!)
                arbeidIFlereLand.yrkesaktivitet = yrkesaktivitet
            }

        bruker.arbeidiflereland = arbeidIFlereLand
        return bruker
    }

    override fun getSedType() = SedType.A008

}
