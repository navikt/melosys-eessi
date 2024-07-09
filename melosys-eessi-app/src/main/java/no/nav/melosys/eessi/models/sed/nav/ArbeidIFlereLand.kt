package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ArbeidIFlereLand(
    var bosted: Bosted? = null,
    var yrkesaktivitet: Yrkesaktivitet? = null
)
