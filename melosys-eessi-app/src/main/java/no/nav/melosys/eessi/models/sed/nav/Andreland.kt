package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Andreland(
    var arbeidsgiver: List<Arbeidsgiver>? = null,
    // A003
    var arbeidsgiveraktivitet: ArbeidsgiverAktivitet? = null
)
