package no.nav.melosys.eessi.service.sed.helpers;

import java.util.HashMap;
import java.util.Map;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.SedType;
import no.nav.melosys.eessi.service.sed.mapper.A009Mapper;
import no.nav.melosys.eessi.service.sed.mapper.SedMapper;

public class SedDataMapperRuter {

    static Map<SedType, Class<? extends SedMapper>> sedMappers = new HashMap<>();

    static {
        sedMappers.put(SedType.A009, A009Mapper.class);
    }

    private SedDataMapperRuter() {}

    public static SedMapper sedMapper(SedType sedType) throws MappingException {
        if (!sedMappers.containsKey(sedType)) {
            throw new MappingException("Sed-type " + sedType.name() + " not supported");
        }
        try {
            return sedMappers.get(sedType).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new MappingException("Could not create mapper with type " + sedType, e);
        }
    }
}
