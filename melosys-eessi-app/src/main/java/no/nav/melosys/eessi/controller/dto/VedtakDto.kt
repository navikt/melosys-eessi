package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class VedtakDto(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var datoForrigeVedtak: LocalDate? = null,
    var erFørstegangsvedtak: Boolean = false
)
