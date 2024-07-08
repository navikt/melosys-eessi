package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class X006FjernInstitusjon(
    var institusjon: Institusjon? = null
)
