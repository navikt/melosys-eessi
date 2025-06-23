package no.nav.melosys.eessi.integration.eux.rina_api.dto.v3

data class RinaSakOversiktV3(
    val sakId: String?,
    val sedListe: List<SedOversikt>?
    // Add other fields as needed based on the full JSON response
)

data class SedOversikt(
    val sedId: String?,
    val sedType: String?,
    val status: String?
)