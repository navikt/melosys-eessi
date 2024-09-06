package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class MedlemskapA008Bruker (
    var arbeidiflereland: ArbeidIFlereLand? = null
)