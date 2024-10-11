package no.nav.melosys.eessi.service.eux

data class OpprettBucOgSedResponse(
    val rinaSaksnummer: String? = null, // TODO: kan ikke være null, tar i egen pr.
    val dokumentId: String? = null // TODO: kan ikke være null, tar i egen pr.
)
