package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class VedtakA009(
    var artikkelforordning: String? = null,
    var gjelderperiode: Periode? = null,
    var gjeldervarighetyrkesaktivitet: String? = null,
    var land: String? = null
) : Vedtak()
