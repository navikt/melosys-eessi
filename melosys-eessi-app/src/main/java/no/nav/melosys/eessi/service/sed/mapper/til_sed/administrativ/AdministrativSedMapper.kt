package no.nav.melosys.eessi.service.sed.mapper.til_sed.administrativ

import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.service.sed.mapper.til_sed.SedMapper

interface AdministrativSedMapper : SedMapper {
    override fun mapTilSed(sedData: SedDataDto, erCDM4_3: Boolean): SED {
        throw MappingException("Kan ikke mappe Administrativ SED fra SedDataDto")
    }
}
