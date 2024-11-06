package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_G_VER
import no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_VER
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA011

class A011Mapper : LovvalgSedMapper<MedlemskapA011> {
    fun mapFraSed(sed: SED): SED {
        val a011 = SED()
        a011.sedType = SedType.A011.toString()
        a011.sedGVer = DEFAULT_SED_G_VER
        a011.sedVer = DEFAULT_SED_VER
        a011.nav = sed.nav
        a011.medlemskap = MedlemskapA011()

        return a011
    }

    override fun getMedlemskap(sedData: SedDataDto): MedlemskapA011 {
        return MedlemskapA011()
    }

    override fun getSedType() = SedType.A011
}

