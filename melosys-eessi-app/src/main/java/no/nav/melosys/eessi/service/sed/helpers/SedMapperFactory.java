package no.nav.melosys.eessi.service.sed.helpers;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.experimental.UtilityClass;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.service.sed.mapper.SedMapper;
import no.nav.melosys.eessi.service.sed.mapper.horisontal.HorisontalSedMapper;
import no.nav.melosys.eessi.service.sed.mapper.lovvalg.*;

@UtilityClass
public class SedMapperFactory {

    static final Map<SedType, SedMapper> SED_MAPPERS =
            Maps.immutableEnumMap(ImmutableMap.<SedType, SedMapper>builder()
                    .put(SedType.A001, new A001Mapper())
                    .put(SedType.A002, new A002Mapper())
                    .put(SedType.A003, new A003Mapper())
                    .put(SedType.A005, new A005Mapper())
                    .put(SedType.A008, new A008Mapper())
                    .put(SedType.A009, new A009Mapper())
                    .put(SedType.A010, new A010Mapper())
                    .put(SedType.A011, new A011Mapper())
                    .put(SedType.A012, new A012Mapper())

                    .put(SedType.H001, new HorisontalSedMapper(SedType.H001))
                    .put(SedType.H003, new HorisontalSedMapper(SedType.H003))
                    .put(SedType.H004, new HorisontalSedMapper(SedType.H004))
                    .put(SedType.H005, new HorisontalSedMapper(SedType.H005))
                    .put(SedType.H010, new HorisontalSedMapper(SedType.H010))
                    .put(SedType.H011, new HorisontalSedMapper(SedType.H011))
                    .put(SedType.H020, new HorisontalSedMapper(SedType.H020))
                    .put(SedType.H061, new HorisontalSedMapper(SedType.H061))
                    .put(SedType.H065, new HorisontalSedMapper(SedType.H065))
                    .put(SedType.H070, new HorisontalSedMapper(SedType.H070))
                    .put(SedType.H120, new HorisontalSedMapper(SedType.H120))
                    .put(SedType.H121, new HorisontalSedMapper(SedType.H121))
                    .put(SedType.H130, new HorisontalSedMapper(SedType.H130))

                    .build());

    public static SedMapper sedMapper(SedType sedType) throws MappingException {
        if (!SED_MAPPERS.containsKey(sedType)) {
            throw new MappingException("Sed-type " + sedType.name() + " støttes ikke");
        }
        return SED_MAPPERS.get(sedType);
    }
}
