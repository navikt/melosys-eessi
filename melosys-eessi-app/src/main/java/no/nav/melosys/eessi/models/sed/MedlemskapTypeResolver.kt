package no.nav.melosys.eessi.models.sed

import com.fasterxml.jackson.annotation.JsonTypeInfo
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.medlemskap.impl.*
import tools.jackson.databind.DatabindContext
import tools.jackson.databind.JavaType
import tools.jackson.databind.jsontype.TypeIdResolver

class MedlemskapTypeResolver : TypeIdResolver {
   private lateinit var sedType: JavaType

    override fun init(javaType: JavaType) {
        this.sedType = javaType
    }

    override fun idFromValue(ctxt: DatabindContext, value: Any): String? = null

    override fun idFromValueAndType(ctxt: DatabindContext, value: Any, suggestedType: Class<*>): String? = null

    override fun idFromBaseType(ctxt: DatabindContext): String? = null

    override fun typeFromId(context: DatabindContext, id: String): JavaType {
        val type = mapping[SedType.valueOf(id)] ?: DEFAULT_CLASS
        return context.constructSpecializedType(sedType, type)
    }

    override fun getDescForKnownTypeIds(): String? = null

    override fun getMechanism(): JsonTypeInfo.Id = JsonTypeInfo.Id.NAME

    companion object {
        private val DEFAULT_CLASS = NoType::class.java
        private val mapping = mapOf(
            SedType.A001 to MedlemskapA001::class.java,
            SedType.A002 to MedlemskapA002::class.java,
            SedType.A003 to MedlemskapA003::class.java,
            SedType.A004 to MedlemskapA004::class.java,
            SedType.A005 to MedlemskapA005::class.java,
            SedType.A006 to MedlemskapA006::class.java,
            SedType.A007 to MedlemskapA007::class.java,
            SedType.A008 to MedlemskapA008::class.java,
            SedType.A009 to MedlemskapA009::class.java,
            SedType.A010 to MedlemskapA010::class.java,
            SedType.A011 to MedlemskapA011::class.java,
            SedType.A012 to MedlemskapA012::class.java,
            SedType.H010 to MedlemskapH010::class.java
        )
    }
}
