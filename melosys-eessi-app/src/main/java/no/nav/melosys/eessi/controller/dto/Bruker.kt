package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import java.time.LocalDate

data class Bruker(
    var fornavn: String? = null,
    var etternavn: String? = null,
    @JsonDeserialize(using = LocalDateDeserializer::class)
    @JsonSerialize(using = LocalDateSerializer::class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var foedseldato: LocalDate, // kaster NullPointerException i Java kode om null
    var kjoenn: String, // kaster NullPointerException i Java kode om null
    var statsborgerskap: Collection<String>, // kaster NullPointerException i Java kode om null
    var fnr: String? = null,
    var harSensitiveOpplysninger: Boolean = false
)
