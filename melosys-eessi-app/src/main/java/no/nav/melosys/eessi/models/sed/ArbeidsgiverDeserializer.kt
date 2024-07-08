package no.nav.melosys.eessi.models.sed

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import no.nav.melosys.eessi.models.sed.nav.Arbeidsgiver

class ArbeidsgiverDeserializer(vc: Class<*>?) : StdDeserializer<List<Arbeidsgiver>>(vc) {

    @Suppress("unused")
    constructor() : this(null)

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): List<Arbeidsgiver> {
        val objectmapper = jsonParser.codec as ObjectMapper
        val jsonNode = objectmapper.readTree<JsonNode>(jsonParser)

        return if (jsonNode.isArray) {
            objectmapper.readerFor(LIST_TYPE_REFERENCE).readValue(jsonNode)
        } else {
            listOf(objectmapper.treeToValue(jsonNode, Arbeidsgiver::class.java))
        }
    }

    companion object {
        private val LIST_TYPE_REFERENCE = object : TypeReference<List<Arbeidsgiver>>() {}
    }
}
