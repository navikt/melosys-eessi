package no.nav.melosys.eessi.service.sed.mapper.horisontal;

import no.nav.melosys.eessi.models.SedType;

public class H020Mapper implements HorisontalSedMapper {

    @Override
    public SedType getSedType() {
        return SedType.H020;
    }
}
