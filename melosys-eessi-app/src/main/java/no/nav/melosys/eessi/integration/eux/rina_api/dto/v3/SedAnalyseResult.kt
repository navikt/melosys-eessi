package no.nav.melosys.eessi.integration.eux.rina_api.dto.v3

enum class SedStatus(val beskrivelse: String) {
    MANGLER_LOKALT("Mangler i lokal database"),
    FINNES_LOKALT("Finnes i lokal database"),
    IKKE_PUBLISERT("Ikke publisert til Kafka"),
    UKJENT_STATUS("Ukjent status")
}

data class SedAnalyseResult(
    val rinaSaksnummer: String,
    val manglendeSeder: List<ManglendeSed>,
    val oversikt: RinaSakOversiktV3? = null
)

data class ManglendeSed(
    val sedType: String,
    val status: SedStatus,
    val dokumentId: String? = null,
    val beskrivelse: String? = null
)
