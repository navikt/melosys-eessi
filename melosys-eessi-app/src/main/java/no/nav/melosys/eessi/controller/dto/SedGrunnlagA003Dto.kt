package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SedGrunnlagA003Dto(
    var overgangsregelbestemmelser: List<Bestemmelse>? = null,
    var norskeArbeidsgivendeVirksomheter: List<Virksomhet>? = null
) : SedGrunnlagDto() {

    constructor(sedGrunnlagDto: SedGrunnlagDto) : this() {
        bostedsadresse = sedGrunnlagDto.bostedsadresse
        utenlandskIdent = sedGrunnlagDto.utenlandskIdent
        arbeidssteder = sedGrunnlagDto.arbeidssteder
        arbeidsland = sedGrunnlagDto.arbeidsland
        arbeidsgivendeVirksomheter = sedGrunnlagDto.arbeidsgivendeVirksomheter
        selvstendigeVirksomheter = sedGrunnlagDto.selvstendigeVirksomheter
        ytterligereInformasjon = sedGrunnlagDto.ytterligereInformasjon
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SedGrunnlagA003Dto) return false
        if (!super.equals(other)) return false

        return overgangsregelbestemmelser == other.overgangsregelbestemmelser &&
            norskeArbeidsgivendeVirksomheter == other.norskeArbeidsgivendeVirksomheter
    }

    override fun hashCode(): Int {
        return listOf(super.hashCode(), overgangsregelbestemmelser, norskeArbeidsgivendeVirksomheter)
            .fold(31) { acc, item -> 31 * acc + (item?.hashCode() ?: 0) }
    }

    override fun toString(): String =
        "SedGrunnlagA003Dto(overgangsregelbestemmelser=$overgangsregelbestemmelser, norskeArbeidsgivendeVirksomheter=$norskeArbeidsgivendeVirksomheter)"
}
