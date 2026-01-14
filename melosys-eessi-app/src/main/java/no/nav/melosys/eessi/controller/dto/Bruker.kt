package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class Bruker(
    var fornavn: String? = null,
    var etternavn: String? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var foedseldato: LocalDate, // kaster NullPointerException i Java kode om null
    var kjoenn: String, // kaster NullPointerException i Java kode om null
    var statsborgerskap: Collection<String>, // kaster NullPointerException i Java kode om null
    var fnr: String? = null,
    var harSensitiveOpplysninger: Boolean = false
)
