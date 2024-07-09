package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Pin(
    var identifikator: String? = null,
    var land: String? = null,
    var sektor: String? = null,
    var institusjonsid: String? = null,
    var institusjonsnavn: String? = null
) {
    // Fjern nå vi har bruk fra kotlin
    constructor(identifikator: String?, land: String?, sektor: String?) : this(
        identifikator = identifikator,
        land = land,
        sektor = sektor,
        institusjonsid = null,
        institusjonsnavn = null
    )
}
