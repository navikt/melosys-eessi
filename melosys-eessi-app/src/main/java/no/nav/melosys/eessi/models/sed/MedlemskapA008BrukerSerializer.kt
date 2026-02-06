package no.nav.melosys.eessi.models.sed

import no.nav.melosys.eessi.models.sed.nav.MedlemskapA008Bruker
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ser.std.StdSerializer

// NB: Custom serializer håndterer alle felt manuelt — nye felt i MedlemskapA008Bruker må legges til her.
class MedlemskapA008BrukerSerializer : StdSerializer<MedlemskapA008Bruker>(MedlemskapA008Bruker::class.java) {

    override fun serialize(value: MedlemskapA008Bruker, gen: JsonGenerator, ctxt: SerializationContext) {
        gen.writeStartObject()
        val arbeid = value.arbeidiflereland
        if (arbeid != null) {
            gen.writeName("arbeidiflereland")
            if (value.cdm44) {
                gen.writeStartArray()
                ctxt.writeValue(gen, arbeid)
                gen.writeEndArray()
            } else {
                ctxt.writeValue(gen, arbeid)
            }
        }
        gen.writeEndObject()
    }
}
