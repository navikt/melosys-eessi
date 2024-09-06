package no.nav.melosys.eessi.models.buc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Creator(
    var organisation: Organisation? = null // TODO: gjør denne none-nullable
)
