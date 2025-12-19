package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import no.nav.melosys.eessi.models.BucType

data class OpprettBucOgSedDtoV2(
    val bucType: BucType,
    val sedDataDto: SedDataDto,
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    val vedlegg: Collection<VedleggReferanse> = emptySet(),
    val sendAutomatisk: Boolean,
    val oppdaterEksisterende: Boolean = false
)
