package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import no.nav.melosys.eessi.models.sed.ArbeidsgiverDeserializer
import tools.jackson.databind.annotation.JsonDeserialize

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Nav(
    var arbeidssted: List<Arbeidssted>? = null,
    var arbeidsland: List<Arbeidsland>? = null,
    var harfastarbeidssted: String? = null,
    var bruker: Bruker? = null,
    var selvstendig: Selvstendig? = null,
    var ytterligereinformasjon: String? = null,

    //Kan forekomme som et enkelt objekt ved feks H001
    @JsonDeserialize(using = ArbeidsgiverDeserializer::class)
    var arbeidsgiver: List<Arbeidsgiver>? = null,
    var sak: Sak? = null,
    var eessisak: EessiSak? = null
)
