package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class SedGrunnlagDto(
    var sedType: String? = null,
    var utenlandskIdent: List<Ident>? = null,
    var bostedsadresse: Adresse? = null,
    var arbeidsgivendeVirksomheter: List<Virksomhet>? = null,
    var selvstendigeVirksomheter: List<Virksomhet>? = null,
    var arbeidssteder: List<Arbeidssted>? = null,
    var arbeidsland: List<Arbeidsland>? = null,
    var harFastArbeidssted: Boolean? = null,
    var lovvalgsperioder: List<Lovvalgsperiode>? = null,
    open var ytterligereInformasjon: String? = null,
    var gjeldenderegler: String? = null
) {
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
