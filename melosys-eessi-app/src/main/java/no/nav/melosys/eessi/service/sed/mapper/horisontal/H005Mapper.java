package no.nav.melosys.eessi.service.sed.mapper.horisontal;

import no.nav.melosys.eessi.models.SedType;

public class H005Mapper implements HorisontalSedMapper {

    @Override
    public SedType getSedType() {
        return SedType.H005;
    }
}
