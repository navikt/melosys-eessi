package no.nav.melosys.eessi.models.sed.nav

data class VedtakA003(
    var gjeldervarighetyrkesaktivitet: String? = null,
    var gjelderperiode: PeriodeA010? = null,
    var land: String? = null
) : Vedtak()
