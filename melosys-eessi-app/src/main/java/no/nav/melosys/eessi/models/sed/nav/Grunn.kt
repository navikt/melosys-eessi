package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Grunn(
    var type: String? = null,
    //Annet settes kun dersom type = 99
    var annet: String? = null
)
