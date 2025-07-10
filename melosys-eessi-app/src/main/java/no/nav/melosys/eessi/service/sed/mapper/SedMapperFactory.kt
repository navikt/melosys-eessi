package no.nav.melosys.eessi.service.sed.mapper

import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.service.sed.mapper.til_sed.SedMapper
import no.nav.melosys.eessi.service.sed.mapper.til_sed.administrativ.X008Mapper
import no.nav.melosys.eessi.service.sed.mapper.til_sed.horisontal.HorisontalSedMapper
import no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg.*

object SedMapperFactory {

    private val SED_MAPPERS: Map<SedType, SedMapper> = mapOf(
        SedType.A001 to A001Mapper(),
        SedType.A002 to A002Mapper(),
        SedType.A003 to A003Mapper(),
        SedType.A004 to A004Mapper(),
        SedType.A005 to A005Mapper(),
        SedType.A008 to A008Mapper(),
        SedType.A009 to A009Mapper(),
        SedType.A010 to A010Mapper(),
        SedType.A011 to A011Mapper(),
        SedType.A012 to A012Mapper(),
        SedType.X008 to X008Mapper(),
        SedType.H001 to HorisontalSedMapper(SedType.H001),
        SedType.H003 to HorisontalSedMapper(SedType.H003),
        SedType.H004 to HorisontalSedMapper(SedType.H004),
        SedType.H005 to HorisontalSedMapper(SedType.H005),
        SedType.H010 to HorisontalSedMapper(SedType.H010),
        SedType.H011 to HorisontalSedMapper(SedType.H011),
        SedType.H020 to HorisontalSedMapper(SedType.H020),
        SedType.H021 to HorisontalSedMapper(SedType.H021),
        SedType.H061 to HorisontalSedMapper(SedType.H061),
        SedType.H062 to HorisontalSedMapper(SedType.H062),
        SedType.H065 to HorisontalSedMapper(SedType.H065),
        SedType.H066 to HorisontalSedMapper(SedType.H066),
        SedType.H070 to HorisontalSedMapper(SedType.H070),
        SedType.H120 to HorisontalSedMapper(SedType.H120),
        SedType.H121 to HorisontalSedMapper(SedType.H121),
        SedType.H130 to HorisontalSedMapper(SedType.H130)
    )

    @JvmStatic
    fun sedMapper(sedType: SedType): SedMapper {
        return SED_MAPPERS[sedType]
            ?: throw MappingException("Sed-type ${sedType.name} støttes ikke")
    }
}
