package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA012

class A012Mapper : LovvalgSedMapper<MedlemskapA012> {
    override fun getSedType() = SedType.A012

    override fun getMedlemskap(sedData: SedDataDto) = MedlemskapA012()
}
