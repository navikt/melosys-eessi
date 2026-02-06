package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.melosys.eessi.models.sed.ArbeidIFlereLandDeserializer
import no.nav.melosys.eessi.models.sed.MedlemskapA008BrukerSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(using = MedlemskapA008BrukerSerializer::class)
data class MedlemskapA008Bruker(
    @JsonDeserialize(using = ArbeidIFlereLandDeserializer::class)
    var arbeidiflereland: ArbeidIFlereLand? = null,
    @JsonIgnore
    var cdm44: Boolean = false
)
