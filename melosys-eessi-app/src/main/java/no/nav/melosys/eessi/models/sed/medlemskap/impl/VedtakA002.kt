package no.nav.melosys.eessi.models.sed.medlemskap.impl

import no.nav.melosys.eessi.models.sed.nav.Periode
import com.fasterxml.jackson.annotation.JsonProperty

data class VedtakA002(
    var annenperiode: Periode? = null,
    var begrunnelse: String? = null,
    @JsonProperty("id")
    var resultat: String? = null
)
