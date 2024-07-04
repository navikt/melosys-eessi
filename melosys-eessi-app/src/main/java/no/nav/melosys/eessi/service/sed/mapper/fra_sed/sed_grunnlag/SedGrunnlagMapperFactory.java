// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;

public final class SedGrunnlagMapperFactory {
    private static final Map<SedType, SedGrunnlagMapper> MAPPERS = Maps.immutableEnumMap(ImmutableMap.<SedType, SedGrunnlagMapper>builder().put(SedType.A003, new SedGrunnlagMapperA003()).put(SedType.A001, new SedGrunnlagMapperA001()).build());

    public static SedGrunnlagMapper getMapper(SedType sedType) {
        if (!MAPPERS.containsKey(sedType)) {
            throw new MappingException("Sed-type " + sedType.name() + " støttes ikke");
        }
        return MAPPERS.get(sedType);
    }

    @java.lang.SuppressWarnings("all")
    private SedGrunnlagMapperFactory() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
