package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Arbeidsland(
    var arbeidssted: List<Arbeidssted> = emptyList(),
    var bosted: ArbeidslandBosted? = null,
    var land: String? = null
)
