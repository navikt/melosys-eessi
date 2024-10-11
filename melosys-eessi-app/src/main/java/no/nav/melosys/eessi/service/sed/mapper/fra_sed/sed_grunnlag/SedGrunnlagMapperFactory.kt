package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag

import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.exception.MappingException

object SedGrunnlagMapperFactory {
    private val MAPPERS: Map<SedType, SedGrunnlagMapper> = mapOf(
        SedType.A003 to SedGrunnlagMapperA003(),
        SedType.A001 to SedGrunnlagMapperA001()
    )

    @JvmStatic
    fun getMapper(sedType: SedType): SedGrunnlagMapper = MAPPERS[sedType]
        ?: throw MappingException("Sed-type ${sedType.name} støttes ikke")
}
