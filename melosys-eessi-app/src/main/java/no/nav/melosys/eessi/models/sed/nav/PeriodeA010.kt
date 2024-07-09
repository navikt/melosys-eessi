package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PeriodeA010(
    var startdato: String? = null,
    var sluttdato: String? = null,
    var aapenperiode: AapenPeriode? = null
) {
    fun erAapenPeriode(): Boolean = aapenperiode != null && aapenperiode?.startdato != null
}
