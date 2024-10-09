package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag

import no.nav.melosys.eessi.controller.dto.SedGrunnlagDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.SED

class SedGrunnlagMapperA001 : SedGrunnlagMapper {
    override fun map(sed: SED): SedGrunnlagDto {
        val sedGrunnlagDto = super.map(sed)
        sedGrunnlagDto.sedType = SedType.A001.name
        return sedGrunnlagDto
    }
}
