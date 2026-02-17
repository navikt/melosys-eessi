package no.nav.melosys.eessi.models.sed.medlemskap.impl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.melosys.eessi.models.sed.nav.Grunnlag

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Forordning8832004(
    var unntak: UnntakForordning? = null,
    var artikkel10: Artikkel10? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UnntakForordning(
    var grunnlag: Grunnlag? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Artikkel10(
    var enighet: Enighet? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Enighet(
    @JsonProperty("EESSIYesNoType")
    var eessiYesNoType: String? = null
)
