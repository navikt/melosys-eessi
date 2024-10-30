package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning

@JsonIgnoreProperties(ignoreUnknown = true)
data class SvarAnmodningUnntakDto(
    var beslutning: SvarAnmodningUnntakBeslutning? = null,
    var begrunnelse: String? = null,
    var delvisInnvilgetPeriode: Periode? = null
)
