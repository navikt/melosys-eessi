package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import java.util.EnumMap;
import java.util.Map;

import lombok.experimental.UtilityClass;
import no.nav.melosys.eessi.models.SedType;

@UtilityClass
public class MelosysEessiMeldingMapperFactory {

    private static final Map<SedType, MelosysEessiMeldingMapper> mappers = new EnumMap<>(SedType.class);
    private static final MelosysEessiMeldingMapper defaultMapper = new DefaultMapper();

    static {
        mappers.put(SedType.A001, new MelosysEessiMeldingMapperA001());
        mappers.put(SedType.A002, new MelosysEessiMeldingMapperA002());
        mappers.put(SedType.A003, new MelosysEessiMeldingMapperA003());
        mappers.put(SedType.A009, new MelosysEessiMeldingMapperA009());
        mappers.put(SedType.A010, new MelosysEessiMeldingMapperA010());
        mappers.put(SedType.A011, new MelosysEessiMeldingMapperA011());
        mappers.put(SedType.X006, new AdministrativSedMapperX006());
    }

    public static MelosysEessiMeldingMapper getMapper(SedType sedType) {
        MelosysEessiMeldingMapper mapper = mappers.get(sedType);

        if (mapper == null) {
            mapper = defaultMapper;
        }

        return mapper;
    }
}
