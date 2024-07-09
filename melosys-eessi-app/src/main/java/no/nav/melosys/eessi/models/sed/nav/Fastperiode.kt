package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Fastperiode (
    var sluttdato: String? = null,
    var startdato: String? = null
)
