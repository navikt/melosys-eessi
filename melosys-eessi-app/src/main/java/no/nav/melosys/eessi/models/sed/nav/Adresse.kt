package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Adresse(
    var by: String? = null,
    var bygning: String? = null,
    var gate: String? = null,
    var land: String? = null,
    var postnummer: String? = null,
    var region: String? = null,
    var type: String? = null
)
