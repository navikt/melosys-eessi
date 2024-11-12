package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_G_VER
import no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_VER
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA011

class A011Mapper : LovvalgSedMapper<MedlemskapA011> {
    override fun getSedType() = SedType.A011

    override fun getMedlemskap(sedData: SedDataDto) = MedlemskapA011()

    fun mapFraSed(sed: SED) = SED(
        sedType = SedType.A011.toString(),
        sedGVer = DEFAULT_SED_G_VER,
        sedVer = DEFAULT_SED_VER,
        nav = sed.nav,
        medlemskap = MedlemskapA011()
    )
}

