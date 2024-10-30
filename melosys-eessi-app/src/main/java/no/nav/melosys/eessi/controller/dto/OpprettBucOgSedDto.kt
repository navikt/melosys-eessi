package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import no.nav.melosys.eessi.models.SedVedlegg

data class OpprettBucOgSedDto(
    var sedDataDto: SedDataDto? = null,
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    var vedlegg: Collection<SedVedlegg> = emptySet()
)
