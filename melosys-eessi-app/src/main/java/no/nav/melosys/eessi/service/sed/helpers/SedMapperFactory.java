package no.nav.melosys.eessi.service.sed.helpers;

import lombok.experimental.UtilityClass;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.service.sed.mapper.SedMapper;
import no.nav.melosys.eessi.service.sed.mapper.horisontal.H005Mapper;
import no.nav.melosys.eessi.service.sed.mapper.lovvalg.*;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

@UtilityClass
public class SedMapperFactory {

    static Map<SedType, Class<? extends SedMapper>> sedMappers = new EnumMap<>(SedType.class);

    static {
        sedMappers.put(SedType.A001, A001Mapper.class);
        sedMappers.put(SedType.A002, A002Mapper.class);
        sedMappers.put(SedType.A003, A003Mapper.class);
        sedMappers.put(SedType.A005, A005Mapper.class);
        sedMappers.put(SedType.A008, A008Mapper.class);
        sedMappers.put(SedType.A009, A009Mapper.class);
        sedMappers.put(SedType.A010, A010Mapper.class);
        sedMappers.put(SedType.A011, A011Mapper.class);
        sedMappers.put(SedType.H005, H005Mapper.class);
    }

    public static SedMapper sedMapper(SedType sedType) throws MappingException {
        if (!sedMappers.containsKey(sedType)) {
            throw new MappingException("Sed-type " + sedType.name() + " not supported");
        }
        try {
            return sedMappers.get(sedType).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new MappingException("Could not create mapper with type " + sedType, e);
        }
    }
}
