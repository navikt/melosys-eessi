package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Foedested(
    var by: String? = null,
    var land: String? = null,
    var region: String? = null
)
