package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.experimental.UtilityClass;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;

@UtilityClass
public class SedGrunnlagMapperFactory {
    private static final Map<SedType, SedGrunnlagMapper> MAPPERS =
            Maps.immutableEnumMap(ImmutableMap.<SedType, SedGrunnlagMapper>builder()
                    .put(SedType.A003, new SedGrunnlagMapperA003())
                    .build());

    public static SedGrunnlagMapper getMapper(SedType sedType) {
        if (!MAPPERS.containsKey(sedType)) {
            throw new MappingException("Sed-type " + sedType.name() + " støttes ikke");
        }
        return MAPPERS.get(sedType);
    }
}
