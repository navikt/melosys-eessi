package no.nav.melosys.eessi.models.sed.medlemskap.impl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap
import no.nav.melosys.eessi.models.sed.nav.Avslag

@JsonIgnoreProperties(ignoreUnknown = true)
data class MedlemskapA004(
    var avslag: Avslag? = null
) : Medlemskap
