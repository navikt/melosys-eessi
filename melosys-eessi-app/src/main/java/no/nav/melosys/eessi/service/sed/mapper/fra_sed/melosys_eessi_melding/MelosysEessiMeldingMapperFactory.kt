package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.models.SedType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class MelosysEessiMeldingMapperFactory(
    @Value("\${rina.institusjon-id}") private val rinaInstitusjonId: String
) {

    private val mappers: MutableMap<SedType, MelosysEessiMeldingMapper> = EnumMap(SedType::class.java)
    private val defaultMapper: MelosysEessiMeldingMapper = DefaultMapper()

    init {
        mappers[SedType.A001] = MelosysEessiMeldingMapperA001()
        mappers[SedType.A002] = MelosysEessiMeldingMapperA002()
        mappers[SedType.A003] = MelosysEessiMeldingMapperA003()
        mappers[SedType.A009] = MelosysEessiMeldingMapperA009()
        mappers[SedType.A010] = MelosysEessiMeldingMapperA010()
        mappers[SedType.A011] = MelosysEessiMeldingMapperA011()
        mappers[SedType.X006] = MelosysEessiMeldingMapperX006(rinaInstitusjonId)
    }

    fun getMapper(sedType: SedType): MelosysEessiMeldingMapper {
        return mappers[sedType] ?: defaultMapper
    }
}
