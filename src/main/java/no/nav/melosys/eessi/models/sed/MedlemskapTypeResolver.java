package no.nav.melosys.eessi.models.sed;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA001;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;

class MedlemskapTypeResolver implements TypeIdResolver {

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

        switch (SedType.valueOf(s)) {
            case A001:
                type = MedlemskapA001.class;
                break;
            case A009:
                type = MedlemskapA009.class;
                break;
            case A002:
            case A003:
            case A004:
            case A005:
            case A006:
            case A007:
            case A008:
            case A010:
            case A011:
            case A012:
            default:
                throw new RuntimeException("Støtte for sed " + s + " er ikke implementert enda");
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
