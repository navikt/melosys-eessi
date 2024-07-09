package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Arbeidsgiver(
    var adresse: Adresse? = null,
    var identifikator: List<Identifikator>? = null,
    var navn: String? = null
)
