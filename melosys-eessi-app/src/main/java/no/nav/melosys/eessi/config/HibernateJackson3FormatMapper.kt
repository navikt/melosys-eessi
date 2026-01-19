package no.nav.melosys.eessi.config

import org.hibernate.type.descriptor.WrapperOptions
import org.hibernate.type.descriptor.java.JavaType
import org.hibernate.type.format.FormatMapper
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.kotlinModule
import java.lang.reflect.Type

/**
 * Custom FormatMapper that uses Jackson 3 (tools.jackson) instead of Jackson 2 (com.fasterxml.jackson).
 * This is needed because Hibernate 7's default JacksonJsonFormatMapper uses Jackson 2,
 * but our application has migrated to Jackson 3 for Spring Boot 4.
 *
 * NB: This class will likely become obsolete when Hibernate releases built-in Jackson 3 support.
 */
class HibernateJackson3FormatMapper : FormatMapper {

    private val jsonMapper: JsonMapper = JsonMapper.builder()
        .addModule(kotlinModule())
        .build()

    override fun <T : Any?> fromString(
        value: CharSequence,
        javaType: JavaType<T>,
        wrapperOptions: WrapperOptions
    ): T {
        val type: Type = javaType.javaType
        return jsonMapper.readValue(value.toString(), jsonMapper.constructType(type))
    }

    override fun <T : Any?> toString(
        value: T,
        javaType: JavaType<T>,
        wrapperOptions: WrapperOptions
    ): String {
        return jsonMapper.writeValueAsString(value)
    }
}
