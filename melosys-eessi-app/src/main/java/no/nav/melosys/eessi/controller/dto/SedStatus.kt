package no.nav.melosys.eessi.controller.dto

enum class SedStatus(val norskStatus: String, val engelskStatus: String) {
    SENDT("SENDT", "SENT"),
    UTKAST("UTKAST", "NEW"),
    TOM("TOM", "EMPTY"),
    MOTTATT("MOTTATT", "RECEIVED"),
    AVBRUTT("AVBRUTT", "CANCELLED");

    companion object {
        @JvmStatic
        fun fraNorskStatus(norskStatus: String?): SedStatus? {
            return if (norskStatus.isNullOrBlank()) null
            else valueOf(norskStatus.uppercase())
        }

        @JvmStatic
        fun fraEngelskStatus(engelskStatus: String?): SedStatus? {
            return if (engelskStatus.isNullOrBlank()) null
            else entries.firstOrNull { it.engelskStatus.equals(engelskStatus, ignoreCase = true) }
        }

        @JvmStatic
        fun erGyldigEngelskStatus(engelskStatus: String?): Boolean {
            return engelskStatus.equals(MOTTATT.engelskStatus, ignoreCase = true) ||
                engelskStatus.equals(SENDT.engelskStatus, ignoreCase = true)
        }
    }
}
