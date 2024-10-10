package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.models.SedType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class MelosysEessiMeldingMapperFactory(
    @Value("\${rina.institusjon-id}") private val rinaInstitusjonId: String
) {

    private val mappers: Map<SedType, MelosysEessiMeldingMapper> = mapOf(
        SedType.A001 to MelosysEessiMeldingMapperA001(),
        SedType.A002 to MelosysEessiMeldingMapperA002(),
        SedType.A003 to MelosysEessiMeldingMapperA003(),
        SedType.A009 to MelosysEessiMeldingMapperA009(),
        SedType.A010 to MelosysEessiMeldingMapperA010(),
        SedType.A011 to MelosysEessiMeldingMapperA011(),
        SedType.X006 to MelosysEessiMeldingMapperX006(rinaInstitusjonId)
    )

    private val defaultMapper: MelosysEessiMeldingMapper = DefaultMapper()

    fun getMapper(sedType: SedType): MelosysEessiMeldingMapper {
        return mappers[sedType] ?: defaultMapper
    }
}
