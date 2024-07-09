package no.nav.melosys.eessi.models.sed.medlemskap.impl

import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class MedlemskapA002(
    var unntak: UnntakA002? = null
) : Medlemskap
