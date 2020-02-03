package no.nav.melosys.eessi.service.sed.helpers;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.experimental.UtilityClass;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.service.sed.mapper.SedMapper;
import no.nav.melosys.eessi.service.sed.mapper.horisontal.*;
import no.nav.melosys.eessi.service.sed.mapper.lovvalg.*;

@UtilityClass
public class SedMapperFactory {

    static final Map<SedType, Class<? extends SedMapper>> SED_MAPPERS =
            Maps.immutableEnumMap(ImmutableMap.<SedType, Class<? extends SedMapper>>builder()
                    .put(SedType.A001, A001Mapper.class)
                    .put(SedType.A002, A002Mapper.class)
                    .put(SedType.A003, A003Mapper.class)
                    .put(SedType.A005, A005Mapper.class)
                    .put(SedType.A008, A008Mapper.class)
                    .put(SedType.A009, A009Mapper.class)
                    .put(SedType.A010, A010Mapper.class)
                    .put(SedType.A011, A011Mapper.class)

                    .put(SedType.H001, H001Mapper.class)
                    .put(SedType.H003, H003Mapper.class)
                    .put(SedType.H004, H004Mapper.class)
                    .put(SedType.H005, H005Mapper.class)
                    .put(SedType.H010, H010Mapper.class)
                    .put(SedType.H011, H011Mapper.class)
                    .put(SedType.H020, H020Mapper.class)
                    .put(SedType.H061, H061Mapper.class)
                    .put(SedType.H065, H065Mapper.class)
                    .put(SedType.H070, H070Mapper.class)
                    .put(SedType.H120, H120Mapper.class)
                    .put(SedType.H121, H121Mapper.class)
                    .put(SedType.H130, H130Mapper.class)

                    .build());

    public static SedMapper sedMapper(SedType sedType) throws MappingException {
        if (!SED_MAPPERS.containsKey(sedType)) {
            throw new MappingException("Sed-type " + sedType.name() + " not supported");
        }
        try {
            return SED_MAPPERS.get(sedType).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new MappingException("Could not create mapper with type " + sedType, e);
        }
    }
}
