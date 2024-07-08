package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Arbeidssted(
    var adresse: Adresse? = null,
    var erikkefastadresse: String? = null,
    var hjemmebase: String? = null,
    var navn: String? = null
)
