package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class VedtakA010(
    var gjeldervarighetyrkesaktivitet: String? = null,
    var gjelderperiode: PeriodeA010? = null,
    var land: String? = null,
) : Vedtak()
