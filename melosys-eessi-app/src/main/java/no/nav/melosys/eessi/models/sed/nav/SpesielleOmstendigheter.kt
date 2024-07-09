package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SpesielleOmstendigheter(
    var type: String? = null,
    var beskrivelseannensituasjon: String? = null
)
