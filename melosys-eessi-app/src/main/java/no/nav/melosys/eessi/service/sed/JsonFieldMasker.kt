package no.nav.melosys.eessi.service.sed

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.LongNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.sed.SED
import org.springframework.stereotype.Component

@Component
class JsonFieldMasker(private val mapper: ObjectMapper) {

    fun sanitizeJson(json: String, whitelist: Set<String>): String {

        @Suppress("kotlin:S6518")
        fun sanitizeNode(node: JsonNode, keep: Set<String>): JsonNode {
            fun maskPrimitive(node: JsonNode): JsonNode {
                return if (node.isNumber) {
                    // Mask numbers as their size-based numeric value
                    val size = node.asText().length
                    val maskedNumber = (1..size).joinToString("") { it.toString() }.toInt()
                    LongNode(maskedNumber.toLong()) // Ensure it's treated as a number
                } else {
                    // Mask text values as 'xxxx...'
                    TextNode("x".repeat(node.asText().length))
                }
            }
            fun sanitizeArrayNode(node: JsonNode, keep: Set<String>): ArrayNode {
                val sanitizedArray = node.deepCopy<ArrayNode>()
                node.forEachIndexed { index, element ->
                    val sanitizedElement: JsonNode = when {
                        element.isTextual -> maskPrimitive(element)
                        element.isInt || element.isLong -> maskPrimitive(element)
                        else -> sanitizeNode(element, keep) // Recursively sanitize
                    }
                    sanitizedArray.set(index, sanitizedElement) // Ensure sanitizedElement is JsonNode
                }
                return sanitizedArray
            }

            fun sanitizeObjectNode(node: JsonNode, keep: Set<String>): ObjectNode {
                val sanitizedObject = node.deepCopy<ObjectNode>()
                node.fieldNames().forEachRemaining { field ->
                    val fieldValue = node[field]
                    if (keep.contains(field)) {
                        sanitizedObject.set<JsonNode>(field, fieldValue) // Preserve whitelisted fields
                    } else {
                        // Mask text and numbers, sanitize nested objects and arrays
                        sanitizedObject.set<JsonNode>(
                            field, when {
                                fieldValue.isTextual -> maskPrimitive(fieldValue)
                                fieldValue.isInt || fieldValue.isLong -> maskPrimitive(fieldValue)
                                else -> sanitizeNode(fieldValue, keep)
                            }
                        )
                    }
                }
                return sanitizedObject
            }

            return when {
                node.isObject -> sanitizeObjectNode(node, keep)
                node.isArray -> sanitizeArrayNode(node, keep)
                node.isBoolean -> node // Preserve boolean values
                node.isInt || node.isLong -> TextNode("n".repeat(node.asText().length)) // Mask integer/long values
                else -> node // Return other types (e.g., null, floats) as-is
            }
        }

        return sanitizeNode(mapper.readTree(json), whitelist).toPrettyString()
    }

    fun toMaskedJson(sedDataDto: SedDataDto): String =
        toMaskedJson(
            sedDataDto, setOf(
                "sedType", "kjoenn", "adressetype", "land", "relasjon",
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
            sanitizeJson(mapper.writeValueAsString(data), keepFields)
        } catch (e: Exception) {
            "Failed to mask JSON: ${e.message}"
        }
}
