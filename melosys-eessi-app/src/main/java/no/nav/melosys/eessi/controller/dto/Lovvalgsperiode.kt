package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape
import java.time.LocalDate

data class Lovvalgsperiode(
    var lovvalgsland: String? = null,
    var unntakFraLovvalgsland: String? = null,
    var bestemmelse: Bestemmelse? = null,
    var tilleggsBestemmelse: Bestemmelse? = null,

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
    var fom: LocalDate? = null,

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
    var tom: LocalDate? = null,

    var unntaksBegrunnelse: String? = null,
    var unntakFraBestemmelse: Bestemmelse?  = null
) {
    fun harTilleggsbestemmelse() = tilleggsBestemmelse != null
}
