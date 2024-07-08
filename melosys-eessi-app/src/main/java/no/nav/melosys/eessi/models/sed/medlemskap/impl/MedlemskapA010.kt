package no.nav.melosys.eessi.models.sed.medlemskap.impl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap
import no.nav.melosys.eessi.models.sed.nav.MeldingOmLovvalg
import no.nav.melosys.eessi.models.sed.nav.Utsendingsland
import no.nav.melosys.eessi.models.sed.nav.VedtakA010

@JsonIgnoreProperties(ignoreUnknown = true)
data class MedlemskapA010(
    var andreland: Utsendingsland? = null,
    var vedtak: VedtakA010? = null,
    var meldingomlovvalg: MeldingOmLovvalg? = null
) : Medlemskap
