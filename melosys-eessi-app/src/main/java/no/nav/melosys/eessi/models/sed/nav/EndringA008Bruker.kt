package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class EndringA008Bruker (
    var fornavn: String? = null,
    var etternavn: String? = null
)

