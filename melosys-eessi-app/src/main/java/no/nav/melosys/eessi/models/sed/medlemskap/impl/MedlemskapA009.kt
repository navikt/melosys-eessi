package no.nav.melosys.eessi.models.sed.medlemskap.impl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap
import no.nav.melosys.eessi.models.sed.nav.Utsendingsland
import no.nav.melosys.eessi.models.sed.nav.VedtakA009

@JsonIgnoreProperties(ignoreUnknown = true)
data class MedlemskapA009(
    var utsendingsland: Utsendingsland? = null,
    var andreland: Utsendingsland? = null,
    var vedtak: VedtakA009? = null
) : Medlemskap
