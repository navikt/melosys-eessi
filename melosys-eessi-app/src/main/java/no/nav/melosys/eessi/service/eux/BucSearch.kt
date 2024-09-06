package no.nav.melosys.eessi.service.eux

data class BucSearch(
    var fnr: String? = null,
    var fornavn: String? = null,
    var etternavn: String? = null,
    var foedselsdato: String? = null,
    var rinaSaksnummer: String? = null,
    var bucType: String? = null,
    var status: String? = null
)
