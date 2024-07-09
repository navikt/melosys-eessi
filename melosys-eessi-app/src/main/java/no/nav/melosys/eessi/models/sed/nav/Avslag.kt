package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Avslag(
    var erbehovformerinformasjon: String? = null,
    var forslagformedlemskap: Land? = null,
    var begrunnelse: String? = null
)
