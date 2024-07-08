package no.nav.melosys.eessi.models.sed.medlemskap.impl

enum class SvarAnmodningUnntakBeslutning(val rinaKode: String?) {
    INNVILGELSE(""),
    DELVIS_INNVILGELSE("godkjent_for_annen_periode"),
    AVSLAG("ikke_godkjent");

    companion object {
        private val rel: Map<String?, SvarAnmodningUnntakBeslutning?> = entries.associateBy { it.rinaKode }

        @JvmStatic
        fun fraRinaKode(rinaKode: String?): SvarAnmodningUnntakBeslutning? {
            return rel[rinaKode]
        }
    }
}
