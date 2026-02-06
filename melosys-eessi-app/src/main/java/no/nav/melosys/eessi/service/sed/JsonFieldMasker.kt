package no.nav.melosys.eessi.service.sed

import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.sed.SED
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import tools.jackson.databind.JsonNode
import tools.jackson.databind.json.JsonMapper

@Component
class JsonFieldMasker(
    private val mapper: JsonMapper,
    @Value("\${NAIS_CLUSTER_NAME:local}") private val naisClusterName: String
) {

    private val isProduction: Boolean get() = naisClusterName.startsWith("prod")

    fun sanitizeJson(json: String, whitelist: Set<String>): String {

        @Suppress("kotlin:S6518")
        fun sanitizeNode(node: JsonNode, keep: Set<String>): JsonNode {
            fun maskPrimitive(node: JsonNode): JsonNode {
                return if (node.isNumber()) {
                    // Mask numbers as their size-based numeric value
                    val size = node.asText().length
                    val maskedNumber = (1..size).joinToString("") { it.toString() }.toInt()
                    mapper.nodeFactory.numberNode(maskedNumber.toLong())
                } else {
                    // Mask text values as 'xxxx...'
                    mapper.nodeFactory.textNode("x".repeat(node.asText().length))
                }
            }
            fun sanitizeArrayNode(node: JsonNode, keep: Set<String>): JsonNode {
                val sanitizedArray = mapper.nodeFactory.arrayNode()
                for (element in node) {
                    val sanitizedElement: JsonNode = when {
                        element.isTextual() -> maskPrimitive(element)
                        element.isInt() || element.isLong() -> maskPrimitive(element)
                        else -> sanitizeNode(element, keep) // Recursively sanitize
                    }
                    sanitizedArray.add(sanitizedElement)
                }
                return sanitizedArray
            }

            fun sanitizeObjectNode(node: JsonNode, keep: Set<String>): JsonNode {
                val sanitizedObject = mapper.nodeFactory.objectNode()
                val fields = node.properties().iterator()
                while (fields.hasNext()) {
                    val entry = fields.next()
                    val field = entry.key
                    val fieldValue = entry.value
                    if (keep.contains(field)) {
                        sanitizedObject.set(field, fieldValue) // Preserve whitelisted fields
                    } else {
                        // Mask text and numbers, sanitize nested objects and arrays
                        sanitizedObject.set(
                            field, when {
                                fieldValue.isTextual() -> maskPrimitive(fieldValue)
                                fieldValue.isInt() || fieldValue.isLong() -> maskPrimitive(fieldValue)
                                else -> sanitizeNode(fieldValue, keep)
                            }
                        )
                    }
                }
                return sanitizedObject
            }

            return when {
                node.isObject() -> sanitizeObjectNode(node, keep)
                node.isArray() -> sanitizeArrayNode(node, keep)
                node.isBoolean() -> node // Preserve boolean values
                node.isInt() || node.isLong() -> mapper.nodeFactory.textNode("n".repeat(node.asText().length)) // Mask integer/long values
                else -> node // Return other types (e.g., null, floats) as-is
            }
        }

        return sanitizeNode(mapper.readTree(json), whitelist).toPrettyString()
    }

    fun toMaskedJson(sedDataDto: SedDataDto): String =
        toMaskedJson(
            sedDataDto, setOf(
                "sedType", "adressetype", "land",
                "fom", "tom", "avklartBostedsland", "datoForrigeVedtak",
                "gsakSaksnummer", "mottakerIder", "beslutning", "nyttLovvalgsland",
                "sedType", "lovvalgsland", "unntakFraBestemmelse", "bestemmelse", "tilleggsBestemmelse",
                "statsborgerskap"
            )
        )

    fun toMaskedJson(sed: SED): String =
        toMaskedJson(
            sed, setOf(
                "sedVer", "sedGVer", "sedType", "sedId", "sedDokumentType", "sedGVer",
                "land", "type", "gjeldendereglerEC883", "gjeldervarighetyrkesaktivitet", "datoforrigevedtak",
                "eropprinneligvedtak", "erendringsvedtak", "sed", "startdato", "sluttdato"
            )
        )

    private fun <T> toMaskedJson(data: T, keepFields: Set<String>): String =
        try {
            val json = mapper.writeValueAsString(data)
            if (isProduction) sanitizeJson(json, keepFields) else mapper.readTree(json).toPrettyString()
        } catch (e: Exception) {
            "Failed to mask JSON: ${e.message}"
        }
}
