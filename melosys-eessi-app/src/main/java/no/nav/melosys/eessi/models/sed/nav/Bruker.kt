package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Bruker(
    var adresse: List<Adresse>? = null,
    var far: Far? = null,
    var mor: Mor? = null,
    var person: Person? = null
)
