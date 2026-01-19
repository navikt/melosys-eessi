package no.nav.melosys.eessi.models.sed

import no.nav.melosys.eessi.models.sed.nav.Arbeidsgiver
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.deser.std.StdDeserializer
import tools.jackson.databind.node.ArrayNode

class ArbeidsgiverDeserializer : StdDeserializer<List<Arbeidsgiver>>(List::class.java) {

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): List<Arbeidsgiver> {
        val jsonNode = deserializationContext.readTree(jsonParser)

        return if (jsonNode.isArray()) {
            val result = mutableListOf<Arbeidsgiver>()
            for (element in jsonNode as ArrayNode) {
                result.add(deserializationContext.readTreeAsValue(element, Arbeidsgiver::class.java))
            }
            result
        } else {
            listOf(deserializationContext.readTreeAsValue(jsonNode, Arbeidsgiver::class.java))
        }
    }
}
