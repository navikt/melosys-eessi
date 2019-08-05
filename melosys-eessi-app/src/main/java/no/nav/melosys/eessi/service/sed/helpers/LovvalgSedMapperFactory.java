package no.nav.melosys.eessi.service.sed.helpers;

import java.util.EnumMap;
import java.util.Map;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.service.sed.mapper.*;

public class LovvalgSedMapperFactory {

    static Map<SedType, Class<? extends LovvalgSedMapper>> sedMappers = new EnumMap<>(SedType.class);

    static {
        sedMappers.put(SedType.A001, A001Mapper.class);
        sedMappers.put(SedType.A003, A003Mapper.class);
        sedMappers.put(SedType.A005, A005Mapper.class);
        sedMappers.put(SedType.A008, A008Mapper.class);
        sedMappers.put(SedType.A009, A009Mapper.class);
    }

    private LovvalgSedMapperFactory() {
    }

    public static LovvalgSedMapper sedMapper(SedType sedType) throws MappingException {
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
