package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Unntak(
    var startdatoansattforsikret: String? = null,
    var grunnlag: Grunnlag? = null,
    var spesielleomstendigheter: SpesielleOmstendigheter? = null,
    var startdatokontraktansettelse: String? = null,
    var begrunnelse: String? = null,
    var a1grunnlag: String? = null
)
