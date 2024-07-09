package no.nav.melosys.eessi.models

import java.util.*
import java.util.stream.Collectors

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

    fun erXSED(): Boolean {
        return name.startsWith("X")
    }

    fun erASED(): Boolean {
        return name.startsWith("A")
    }

    fun kreverAdresse(): Boolean {
        return KREVER_ADRESSE.stream().anyMatch { s: SedType -> s == this }
    }

    companion object {
        private val LOVVALG_SED_TYPER: Collection<SedType> = Arrays.stream(entries.toTypedArray())
            .filter { s: SedType -> s.name.startsWith("A") }
            .collect(Collectors.toSet())

        @JvmStatic
        fun erLovvalgSed(sedType: String?): Boolean {
            return LOVVALG_SED_TYPER.stream().anyMatch { s: SedType -> s.name == sedType }
        }

        val KREVER_ADRESSE: List<SedType> = Arrays.asList(A001, A002, A003, A004, A007, A009, A010)
    }
}
