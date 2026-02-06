package no.nav.melosys.eessi.models.sed

import no.nav.melosys.eessi.models.sed.nav.ArbeidIFlereLand
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.deser.std.StdDeserializer

class ArbeidIFlereLandDeserializer : StdDeserializer<ArbeidIFlereLand>(ArbeidIFlereLand::class.java) {

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): ArbeidIFlereLand {
        val jsonNode = deserializationContext.readTree(jsonParser)

        return if (jsonNode.isArray()) {
            deserializationContext.readTreeAsValue(jsonNode.iterator().next(), ArbeidIFlereLand::class.java)
        } else {
            deserializationContext.readTreeAsValue(jsonNode, ArbeidIFlereLand::class.java)
        }
    }
}
