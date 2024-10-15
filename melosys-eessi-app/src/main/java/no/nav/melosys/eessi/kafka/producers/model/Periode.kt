package no.nav.melosys.eessi.kafka.producers.model

import java.time.LocalDate

data class Periode(
    var fom: LocalDate? = null,
    var tom: LocalDate? = null
)
