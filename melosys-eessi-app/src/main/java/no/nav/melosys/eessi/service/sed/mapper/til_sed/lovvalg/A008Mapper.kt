package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA008
import no.nav.melosys.eessi.models.sed.nav.ArbeidIFlereLand
import no.nav.melosys.eessi.models.sed.nav.Bosted
import no.nav.melosys.eessi.models.sed.nav.MedlemskapA008Bruker
import no.nav.melosys.eessi.models.sed.nav.Yrkesaktivitet

class A008Mapper : LovvalgSedMapper<MedlemskapA008> {

    override fun getSedType() = SedType.A008

    override fun getMedlemskap(sedData: SedDataDto) = MedlemskapA008(
        bruker = hentA008Bruker(sedData)
    )

    private fun hentA008Bruker(sedData: SedDataDto) =
        MedlemskapA008Bruker(
            arbeidiflereland = ArbeidIFlereLand(
                bosted = Bosted(sedData.avklartBostedsland),
                yrkesaktivitet = sedData.søknadsperiode?.fom?.let { søknadsperiodeFom ->
                    Yrkesaktivitet(startdato = søknadsperiodeFom.formaterEllerNull())
                }
            ))
}
