package no.nav.melosys.eessi.models.sed.medlemskap.impl

import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap
import no.nav.melosys.eessi.models.sed.nav.*
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class MedlemskapA001(
    var unntak: Unntak? = null,
    var vertsland: Vertsland? = null,
    var soeknadsperiode: Fastperiode? = null,
    var tidligereperiode: MutableList<Periode?>? = null,
    var naavaerendemedlemskap: MutableList<Land?>? = null,
    var forespurtmedlemskap: MutableList<Land?>? = null,
    var anmodning: Anmodning? = null,
    var forrigesoeknad: MutableList<ForrigeSoeknad?>? = null,
    var forordning8832004: Forordning8832004? = null,
    var rammeavtale: Rammeavtale? = null
) : Medlemskap
