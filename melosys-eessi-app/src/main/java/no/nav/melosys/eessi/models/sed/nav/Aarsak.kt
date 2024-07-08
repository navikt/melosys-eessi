package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Aarsak(
    var type: String? = null,
    var annet: String? = null
)
