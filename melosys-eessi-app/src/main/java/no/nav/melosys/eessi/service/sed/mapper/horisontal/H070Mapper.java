package no.nav.melosys.eessi.service.sed.mapper.horisontal;

import no.nav.melosys.eessi.models.SedType;

public class H070Mapper implements HorisontalSedMapper {

    @Override
    public SedType getSedType() {
        return SedType.H070;
    }
}
