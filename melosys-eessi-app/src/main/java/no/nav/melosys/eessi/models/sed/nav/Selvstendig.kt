package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Selvstendig (
    var arbeidsgiver: List<Arbeidsgiver>? = null
)
