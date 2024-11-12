package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA005

class A005Mapper : LovvalgSedMapper<MedlemskapA005> {
    override fun getSedType() = SedType.A005

    override fun getMedlemskap(sedData: SedDataDto): MedlemskapA005 = MedlemskapA005()
}
