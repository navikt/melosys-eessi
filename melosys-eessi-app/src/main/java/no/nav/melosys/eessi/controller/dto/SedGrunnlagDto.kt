package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Dto for å hente SedGrunnlag fra eux-rina
 * SedGrunnlagDto lages av SedGrunnlagMapper og serialiserer til JSON. Deserialiserer ikke JSON til SedGrunnlagDto.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
open class SedGrunnlagDto(
    var sedType: String? = null,
    var utenlandskIdent: List<Ident> = emptyList(),
    var bostedsadresse: Adresse? = null,
    var arbeidsgivendeVirksomheter: List<Virksomhet> = emptyList(),
    var selvstendigeVirksomheter: List<Virksomhet> = emptyList(),
    var arbeidssteder: List<Arbeidssted> = emptyList(),
    var arbeidsland: List<Arbeidsland> = emptyList(),
    var harFastArbeidssted: Boolean? = null,
    var lovvalgsperioder: List<Lovvalgsperiode>? = null,
    var ytterligereInformasjon: String? = null,
    var gjeldenderegler: String? = null
)  {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SedGrunnlagDto) return false

        return sedType == other.sedType &&
            utenlandskIdent == other.utenlandskIdent &&
            bostedsadresse == other.bostedsadresse &&
            arbeidsgivendeVirksomheter == other.arbeidsgivendeVirksomheter &&
            selvstendigeVirksomheter == other.selvstendigeVirksomheter &&
            arbeidssteder == other.arbeidssteder &&
            arbeidsland == other.arbeidsland &&
            harFastArbeidssted == other.harFastArbeidssted &&
            lovvalgsperioder == other.lovvalgsperioder &&
            ytterligereInformasjon == other.ytterligereInformasjon &&
            gjeldenderegler == other.gjeldenderegler
    }

    override fun hashCode(): Int {
        return listOf(
            sedType,
            utenlandskIdent,
            bostedsadresse,
            arbeidsgivendeVirksomheter,
            selvstendigeVirksomheter,
            arbeidssteder,
            arbeidsland,
            harFastArbeidssted,
            lovvalgsperioder,
            ytterligereInformasjon,
            gjeldenderegler
        ).fold(31) { acc, item -> 31 * acc + (item?.hashCode() ?: 0) }
    }

    override fun toString(): String =
        "SedGrunnlagDto(sedType=$sedType, utenlandskIdent=$utenlandskIdent, bostedsadresse=$bostedsadresse, arbeidsgivendeVirksomheter=$arbeidsgivendeVirksomheter, selvstendigeVirksomheter=$selvstendigeVirksomheter, arbeidssteder=$arbeidssteder, arbeidsland=$arbeidsland, harFastArbeidssted=$harFastArbeidssted, lovvalgsperioder=$lovvalgsperioder, ytterligereInformasjon=$ytterligereInformasjon, gjeldenderegler=$gjeldenderegler)"
}
