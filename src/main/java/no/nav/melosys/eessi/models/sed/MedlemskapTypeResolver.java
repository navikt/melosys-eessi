package no.nav.melosys.eessi.models.sed;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.*;

class MedlemskapTypeResolver implements TypeIdResolver {

    private static final Class<? extends Medlemskap> DEFAULT_CLASS = NoType.class;
    private static final List<String> SED_TYPES_STRING = Arrays.stream(SedType.values()).map(SedType::name).collect(Collectors.toList());

    private static final EnumMap<SedType, Class<? extends Medlemskap>> medlemskapMapper = new EnumMap<>(SedType.class);

    static {
        medlemskapMapper.put(SedType.A001, MedlemskapA001.class);
        medlemskapMapper.put(SedType.A002, MedlemskapA002.class);
        medlemskapMapper.put(SedType.A003, MedlemskapA003.class);
        medlemskapMapper.put(SedType.A004, MedlemskapA004.class);
        medlemskapMapper.put(SedType.A005, MedlemskapA005.class);
        medlemskapMapper.put(SedType.A006, MedlemskapA006.class);
        medlemskapMapper.put(SedType.A007, MedlemskapA007.class);
        //A008
        medlemskapMapper.put(SedType.A009, MedlemskapA009.class);
        medlemskapMapper.put(SedType.A010, MedlemskapA010.class);
        medlemskapMapper.put(SedType.A011, MedlemskapA011.class);
        medlemskapMapper.put(SedType.A012, MedlemskapA012.class);
    }

    private JavaType sedType;

    @Override
    public void init(JavaType javaType) {
        this.sedType = javaType;
    }

    @Override
    public String idFromValue(Object o) {
        return null;
    }

    @Override
    public String idFromValueAndType(Object o, Class<?> aClass) {
        return null;
    }

    @Override
    public String idFromBaseType() {
        return null;
    }

    @Override
    public JavaType typeFromId(DatabindContext databindContext, String s) {
        Class<?> type;
        if (SED_TYPES_STRING.contains(s)) {
            type = medlemskapMapper.get(SedType.valueOf(s));
        } else {
            type = DEFAULT_CLASS;
        }

        return databindContext.constructSpecializedType(sedType, type);
    }

    @Override
    public String getDescForKnownTypeIds() {
        return null;
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.NAME;
    }
}
