package no.nav.melosys.eessi.models

data class SedKontekst(
    var isForsoktIdentifisert: Boolean = false,
    var navIdent: String? = null,
    var journalpostID: String? = null,
    var dokumentID: String? = null,
    var gsakSaksnummer: String? = null,
    var oppgaveID: String? = null,
    var isPublisertKafka: Boolean = false
)
