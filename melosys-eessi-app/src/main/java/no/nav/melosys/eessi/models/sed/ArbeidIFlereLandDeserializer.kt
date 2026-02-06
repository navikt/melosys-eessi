package no.nav.melosys.eessi.models.sed

import no.nav.melosys.eessi.models.sed.nav.ArbeidIFlereLand
import org.slf4j.LoggerFactory
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.deser.std.StdDeserializer

class ArbeidIFlereLandDeserializer : StdDeserializer<ArbeidIFlereLand>(ArbeidIFlereLand::class.java) {

    private val log = LoggerFactory.getLogger(ArbeidIFlereLandDeserializer::class.java)

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): ArbeidIFlereLand? {
        val jsonNode = deserializationContext.readTree(jsonParser)

        return if (jsonNode.isArray()) {
            if (jsonNode.isEmpty()) {
                log.warn("arbeidiflereland-array er tomt, returnerer null")
                return null
            }
            if (jsonNode.size() > 1) {
                log.warn("arbeidiflereland-array har {} elementer, kun første element brukes", jsonNode.size())
            }
            deserializationContext.readTreeAsValue(jsonNode.iterator().next(), ArbeidIFlereLand::class.java)
        } else {
            deserializationContext.readTreeAsValue(jsonNode, ArbeidIFlereLand::class.java)
        }
    }
}
