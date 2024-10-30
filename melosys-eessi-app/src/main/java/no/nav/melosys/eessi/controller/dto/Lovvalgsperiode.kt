package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import java.time.LocalDate

data class Lovvalgsperiode(
    var lovvalgsland: String? = null,
    var unntakFraLovvalgsland: String? = null,
    var bestemmelse: Bestemmelse? = null,
    var tilleggsBestemmelse: Bestemmelse? = null,

    @JsonDeserialize(using = LocalDateDeserializer::class)
    @JsonSerialize(using = LocalDateSerializer::class)
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
    var fom: LocalDate? = null,

    @JsonDeserialize(using = LocalDateDeserializer::class)
    @JsonSerialize(using = LocalDateSerializer::class)
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
    var tom: LocalDate? = null,

    var unntaksBegrunnelse: String? = null,
    var unntakFraBestemmelse: Bestemmelse?  = null
) {
    fun harTilleggsbestemmelse() = tilleggsBestemmelse != null
}
