package no.nav.melosys.eessi.kafka.producers.model

data class AnmodningUnntak(
    var unntakFraLovvalgsland: String? = null,
    var unntakFraLovvalgsbestemmelse: String? = null,
    var erFjernarbeidTWFA: Boolean? = null
)
