package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Sak(
    var anmodning: X001Anmodning? = null,
    var kontekst: Kontekst? = null,
    var fjerninstitusjon: X006FjernInstitusjon? = null,
    var ugyldiggjoere: Ugyldiggjoere? = null
)
