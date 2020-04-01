package no.nav.melosys.eessi.service.sed.mapper.til_sed.horisontal;

import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.service.sed.mapper.til_sed.SedMapper;

public class HorisontalSedMapper implements SedMapper {
    private final SedType sedType;

    public HorisontalSedMapper(SedType sedType) {
        this.sedType = sedType;
    }

    @Override
    public SedType getSedType() {
       return sedType;
    }
}
