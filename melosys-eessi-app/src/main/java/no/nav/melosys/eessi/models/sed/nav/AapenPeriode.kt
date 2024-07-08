package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AapenPeriode  @JsonCreator constructor(
    @JsonProperty("type") var ukjentEllerÅpenSluttdato: String? = null,
    @JsonProperty("startdato") var startdato: String? = null
)
