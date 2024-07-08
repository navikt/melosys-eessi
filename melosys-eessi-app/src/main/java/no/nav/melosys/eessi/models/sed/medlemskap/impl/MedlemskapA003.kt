package no.nav.melosys.eessi.models.sed.medlemskap.impl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap
import no.nav.melosys.eessi.models.sed.nav.Andreland
import no.nav.melosys.eessi.models.sed.nav.VedtakA003

@JsonIgnoreProperties(ignoreUnknown = true)
data class MedlemskapA003(
    var gjeldendereglerEC883: MutableList<String?>? = null,
    var relevantartikkelfor8832004eller9872009: String? = null,
    var andreland: Andreland? = null,
    var vedtak: VedtakA003? = null,
    var isDeterminationProvisional: String? = null
) : Medlemskap
