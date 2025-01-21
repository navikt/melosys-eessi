package no.nav.melosys.eessi.service.sed

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.sed.SED
import org.springframework.stereotype.Component

@Component
class JsonFieldMasker(private val mapper: ObjectMapper) {

    fun sanitizeJson(json: String, keep: Set<String>): String {

        // Writen with help of ChatGTP
        @Suppress("kotlin:S6518")
        fun sanitizeNode(node: JsonNode, keep: Set<String>, fieldName: String? = null): JsonNode {
            return when {
                node.isObject -> {
                    val sanitizedObject = node.deepCopy<ObjectNode>()
                    node.fieldNames().forEachRemaining { field ->
                        val fieldValue = node[field]
                        if (!keep.contains(field)) {
                            when {
                                fieldValue.isTextual -> {
                                    sanitizedObject.put(field, "x".repeat(fieldValue.asText().length))
                                }

                                fieldValue.isInt || fieldValue.isLong -> {
                                    sanitizedObject.put(field, "x".repeat(fieldValue.asText().length))
                                }

                                else -> {
                                    sanitizedObject.set(field, sanitizeNode(fieldValue, keep, field))
                                }
                            }
                        } else {
                            sanitizedObject.set(field, fieldValue) // Preserve whitelisted fields
                        }
                    }
                    sanitizedObject
                }

                node.isArray -> {
                    val sanitizedArray = node.deepCopy<ArrayNode>()
                    node.forEachIndexed { index, element ->
                        if (element.isTextual) {
                            // Mask textual elements in arrays
                            sanitizedArray.set(index, TextNode("x".repeat(element.asText().length)))
                        } else if (element.isInt || element.isLong) {
                            // Mask integer/long elements in arrays
                            sanitizedArray.set(index, TextNode("x".repeat(element.asText().length)))
                        } else {
                            // Recursively sanitize other types of elements
                            sanitizedArray.set(index, sanitizeNode(element, keep))
                        }
                    }
                    sanitizedArray
                }

                node.isBoolean -> {
                    // Preserve boolean values as-is
                    node
                }

                node.isInt || node.isLong -> {
                    // Mask integer/long values
                    TextNode("x".repeat(node.asText().length))
                }

                else -> node // Return other types (null, floats, etc.) as-is
            }
        }
        return sanitizeNode(mapper.readTree(json), keep).toPrettyString()
    }

    fun toMaskedJson(sedDataDto: SedDataDto): String =
        try {
            mapper.writeValueAsString(sedDataDto).let {
                return sanitizeJson(
                    it, keep = setOf(
                        "sedType", "kjoenn", "adressetype", "land", "relasjon",
                        "fom", "tom", "avklartBostedsland", "datoForrigeVedtak",
                        "gsakSaksnummer", "mottakerIder", "beslutning", "nyttLovvalgsland",
                        "sedType", "lovvalgsland", "unntakFraBestemmelse", "bestemmelse", "tilleggsBestemmelse",
                        "statsborgerskap"
                    )
                )
            }
        } catch (e: Exception) {
            "Failed to mask JSON: ${e.message}"
        }

    fun toMaskedJson(sed: SED): String =
        try {
            mapper.writeValueAsString(sed).let {
                return sanitizeJson(
                    it, keep = setOf(
                        "sedVer", "sedGVer", "sedType", "sedId", "sedDokumentType", "sedGVer",
                        "land", "type", "gjeldendereglerEC883", "gjeldervarighetyrkesaktivitet", "datoforrigevedtak",
                        "eropprinneligvedtak", "erendringsvedtak", "sed"
                    )
                )
            }
        } catch (
            e: Exception
        ) {
            "Failed to mask JSON: ${e.message}"
        }
}
