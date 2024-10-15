package no.nav.melosys.eessi.kafka.producers.model

import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning

data class SvarAnmodningUnntak(
    var beslutning: SvarAnmodningUnntakBeslutning? = null,
    var begrunnelse: String? = null,
    var delvisInnvilgetPeriode: Periode? = null
)
