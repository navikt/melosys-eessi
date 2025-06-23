package no.nav.melosys.eessi.integration.eux.rina_api.dto

data class SedOversikt(
    val sedTittel: String? = null,
    val sedType: String? = null,
    val sedId: String? = null,
    val sedIdParent: String? = null,
    val status: String? = null,
    val sistEndretDato: String? = null,
    val svarsedType: String? = null,
    val svarsedId: String? = null,
    val sedHandlinger: List<String>? = null,
    val vedlegg: List<Vedlegg>? = null,
    val leveranseStatus: String? = null
)
