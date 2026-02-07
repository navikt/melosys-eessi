package no.nav.melosys.eessi.models.sed

import com.fasterxml.jackson.annotation.*
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap
import no.nav.melosys.eessi.models.sed.nav.*
import tools.jackson.databind.annotation.JsonTypeIdResolver
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder("nav", "medlemskap", "sed", "sedVer", "sedGVer")
data class SED(
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "sed")
    @JsonTypeIdResolver(MedlemskapTypeResolver::class)
    @JsonProperty("medlemskap") var medlemskap: Medlemskap? = null,
    @JsonProperty("nav") var nav: Nav? = null,
    @JsonProperty("sed") var sedType: String? = null,
    @JsonProperty("sedVer") var sedVer: String? = null,
    @JsonProperty("sedGVer") var sedGVer: String? = null
) {
    fun finnPerson(): Optional<Person> = if (erXSED()) {
        Optional.ofNullable(nav?.sak?.kontekst?.bruker?.person)
    } else {
        Optional.ofNullable(nav?.bruker?.person)
    }

    fun erXSED(): Boolean = SedType.valueOf(sedType ?: "").erXSED()
}
