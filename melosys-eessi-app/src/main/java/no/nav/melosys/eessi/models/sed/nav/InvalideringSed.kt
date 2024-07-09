package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class InvalideringSed(
    var utstedelsesdato: String? = null,
    var type: String? = null,
    var grunn: Grunn? = null
)
