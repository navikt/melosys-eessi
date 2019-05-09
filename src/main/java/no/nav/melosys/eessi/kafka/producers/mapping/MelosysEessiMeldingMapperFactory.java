package no.nav.melosys.eessi.kafka.producers.mapping;

import java.util.EnumMap;
import java.util.Map;
import no.nav.melosys.eessi.models.SedType;

public class MelosysEessiMeldingMapperFactory {

    private MelosysEessiMeldingMapperFactory() {}

    private static final Map<SedType, MelosysEessiMeldingMapper> mappers = new EnumMap<>(SedType.class);

    static {
        mappers.put(SedType.A009, new MelosysEessiMeldingMapperA009());
        mappers.put(SedType.A010, new MelosysEessiMeldingMapperA010());
    }

    public static MelosysEessiMeldingMapper getMapper(SedType sedType) {
        return mappers.get(sedType);
    }

}
