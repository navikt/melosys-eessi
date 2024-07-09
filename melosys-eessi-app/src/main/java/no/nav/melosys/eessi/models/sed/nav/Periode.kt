package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Periode(
    var aapenperiode: AapenPeriode? = null,
    var fastperiode: Fastperiode? = null
) {
    fun erAapenPeriode(): Boolean = aapenperiode != null && aapenperiode?.startdato != null
}
