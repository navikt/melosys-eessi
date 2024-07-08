package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class EndringA008(
    var periode: String? = null,
    var arbeidssted: Adresse? = null,
    var adresse: Adresse? = null,
    var bruker: EndringA008Bruker? = null,
    var trerikraftfra: String? = null,
    var startdato: String? = null,
    var sluttdato: String? = null
)
