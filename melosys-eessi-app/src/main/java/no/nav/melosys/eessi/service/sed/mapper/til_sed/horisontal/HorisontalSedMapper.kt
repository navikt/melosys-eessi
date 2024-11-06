package no.nav.melosys.eessi.service.sed.mapper.til_sed.horisontal

import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.service.sed.mapper.til_sed.SedMapper

class HorisontalSedMapper(private val sedType: SedType) : SedMapper {
    override fun getSedType(): SedType {
        return sedType
    }
}
