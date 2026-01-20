package no.nav.melosys.eessi.models.sed.medlemskap.impl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap
import no.nav.melosys.eessi.models.sed.nav.EndringA008
import no.nav.melosys.eessi.models.sed.nav.MedlemskapA008Bruker

@JsonIgnoreProperties(ignoreUnknown = true)
data class MedlemskapA008(
    var endring: EndringA008? = null,
    var bruker: MedlemskapA008Bruker? = null,
    var formaal: String? = null // CDM 4.4: "endringsmelding" | "arbeid_flere_land"
) : Medlemskap
