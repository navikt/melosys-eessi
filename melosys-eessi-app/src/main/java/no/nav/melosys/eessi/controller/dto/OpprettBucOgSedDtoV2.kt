package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import jakarta.validation.constraints.NotNull
import no.nav.melosys.eessi.models.BucType

data class OpprettBucOgSedDtoV2(
    @field:NotNull
    val bucType: BucType,
    @field:NotNull
    val sedDataDto: SedDataDto,
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    val vedlegg: List<VedleggReferanse> = emptyList(),
    @field:NotNull
    val sendAutomatisk: Boolean,
    val oppdaterEksisterende: Boolean = false
)
