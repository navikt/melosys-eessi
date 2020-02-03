package no.nav.melosys.eessi.service.sed.mapper.horisontal;

import no.nav.melosys.eessi.models.SedType;

public class H004Mapper implements HorisontalSedMapper {

    @Override
    public SedType getSedType() {
        return SedType.H004;
    }
}
