package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UtpekingAvvisDto(
    var nyttLovvalgsland: String? = null,
    var begrunnelseUtenlandskMyndighet: String? = null,
    var vilSendeAnmodningOmMerInformasjon: Boolean = false
)
