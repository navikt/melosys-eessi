package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class MedlemskapA008Bruker (
    /**
     * CDM 4.3: ArbeidIFlereLand (enkelt objekt)
     * CDM 4.4: List<ArbeidIFlereLand> (array)
     *
     * Type bestemmes av CDM_4_4 toggle ved mapping i A008Mapper.
     */
    var arbeidiflereland: Any? = null
)
