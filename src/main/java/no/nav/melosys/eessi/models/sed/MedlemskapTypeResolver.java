package no.nav.melosys.eessi.models.sed;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.*;

@Slf4j
class MedlemskapTypeResolver implements TypeIdResolver {

    private static final Class<? extends Medlemskap> DEFAULT_CLASS = NoType.class;
    private static final List<String> SED_TYPES_STRING = Arrays.stream(SedType.values()).map(SedType::name).collect(Collectors.toList());

    private static final EnumMap<SedType, Class<? extends Medlemskap>> mapping = new EnumMap<>(SedType.class);

    static {
        mapping.put(SedType.A001, MedlemskapA001.class);
        mapping.put(SedType.A002, MedlemskapA002.class);
        mapping.put(SedType.A003, MedlemskapA003.class);
        mapping.put(SedType.A004, MedlemskapA004.class);
        mapping.put(SedType.A005, MedlemskapA005.class);
        mapping.put(SedType.A006, MedlemskapA006.class);
        mapping.put(SedType.A007, MedlemskapA007.class);
        mapping.put(SedType.A008, MedlemskapA008.class);
        mapping.put(SedType.A009, MedlemskapA009.class);
        mapping.put(SedType.A010, MedlemskapA010.class);
        mapping.put(SedType.A011, MedlemskapA011.class);
        mapping.put(SedType.A012, MedlemskapA012.class);
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
            type = mapping.get(SedType.valueOf(s));
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
