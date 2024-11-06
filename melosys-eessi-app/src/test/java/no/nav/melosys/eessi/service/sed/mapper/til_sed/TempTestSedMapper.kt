package no.nav.melosys.eessi.service.sed.mapper.til_sed

import no.nav.melosys.eessi.models.SedType

// fjerns når vi koverterer til Kotlin
class TempTestSedMapper : SedMapper {
    override fun getSedType(): SedType {
        return SedType.A003
    }
}
