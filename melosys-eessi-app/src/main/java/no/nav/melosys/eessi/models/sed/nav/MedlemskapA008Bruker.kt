package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class MedlemskapA008Bruker(
    // Any? fordi CDM 4.3 sender ArbeidIFlereLand (objekt) mens CDM 4.4 sender List<ArbeidIFlereLand> (array).
    // Ved deserialisering blir dette LinkedHashMap (objekt) eller List<LinkedHashMap> (array).
    var arbeidiflereland: Any? = null
)
