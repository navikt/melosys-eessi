package no.nav.melosys.eessi.models.sed.medlemskap.impl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Rammeavtale(var fjernarbeid: Fjernarbeid? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Fjernarbeid(
    @JsonProperty("EESSIYesNoType")
    var eessiYesNoType: String? = null
)
