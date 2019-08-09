package no.nav.melosys.eessi.service.sed.mapper.lovvalg;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.service.sed.mapper.SedMapper;

/**
 * Felles mapper-interface for alle typer av lovvalgs-SED'er. Mapper NAV-objektet i NAV-SED, som brukes av eux for
 * å plukke ut nødvendig informasjon for en angitt SED
 */
public interface LovvalgSedMapper<T extends Medlemskap> extends SedMapper {

    // Hvis det skulle trenges noen spesifikke endringer av NAV-objektet for enkelte SED'er,
    // bør denne metoden overrides.
    @Override
    default SED mapTilSed(SedDataDto sedData) throws MappingException, NotFoundException {
        SED sed = SedMapper.super.mapTilSed(sedData);
        sed.setMedlemskap(getMedlemskap(sedData));

        return sed;
    }

    T getMedlemskap(SedDataDto sedData) throws MappingException, NotFoundException;
}
