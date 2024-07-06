package no.nav.melosys.eessi.models.buc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Organisation(
    var name: String? = null,
    var countryCode: String? = null, // TODO: gjør denne none-nullable
    var id: String? = null // TODO: gjør denne none-nullable
)
