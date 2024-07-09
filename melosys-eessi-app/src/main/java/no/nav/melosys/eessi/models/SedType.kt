package no.nav.melosys.eessi.models

enum class SedType {
    X001,
    X002,
    X003,
    X004,
    X005,
    X006,
    X007,
    X008,
    X009,
    X010,
    X011,
    X012,
    X013,
    X050,
    X100,

    A001,
    A002,
    A003,
    A004,
    A005,
    A006,
    A007,
    A008,
    A009,
    A010,
    A011,
    A012,

    H001,
    H002,
    H003,
    H004,
    H005,
    H006,
    H010,
    H011,
    H012,
    H020,
    H021,
    H061,
    H062,
    H065,
    H066,
    H070,
    H120,
    H121,
    H130,

    S040,
    S041;

    fun erXSED(): Boolean = name.startsWith("X")

    fun erASED(): Boolean = name.startsWith("A")

    fun kreverAdresse(): Boolean = this in KREVER_ADRESSE

    companion object {
        private val LOVVALG_SED_TYPER: Set<SedType> = entries.filter { it.name.startsWith("A") }.toSet()
        private val KREVER_ADRESSE = setOf(A001, A002, A003, A004, A007, A009, A010)

        @JvmStatic
        fun erLovvalgSed(sedType: String?): Boolean = LOVVALG_SED_TYPER.any { it.name == sedType }
    }
}
