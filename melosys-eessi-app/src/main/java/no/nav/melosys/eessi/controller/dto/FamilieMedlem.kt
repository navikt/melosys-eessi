package no.nav.melosys.eessi.controller.dto

data class FamilieMedlem(
    var relasjon: String? = null,  // FAR or MOR
    var fornavn: String? = null,
    var etternavn: String? = null
)
