package no.nav.melosys.eessi.service.sed.mapper.administrativ;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.sed.mapper.SedMapper;

public interface AdministrativSedMapper extends SedMapper {
    @Override
    default SED mapTilSed(SedDataDto sedData) throws MappingException {
        throw new MappingException("Kan ikke mappe Administrativ SED fra SedDataDto");
    }
}
