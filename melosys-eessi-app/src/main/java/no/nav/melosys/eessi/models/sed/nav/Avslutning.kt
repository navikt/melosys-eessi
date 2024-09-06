package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Avslutning(
    var dato: String? = null,
    var aarsak: Aarsak? = null,
    var type: String? = null
)
