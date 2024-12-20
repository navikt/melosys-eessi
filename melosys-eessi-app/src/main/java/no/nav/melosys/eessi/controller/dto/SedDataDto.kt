package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls

/**
 * bruke for å overføre SED-data fra melosys-api
 * Deserialiserer JSON til objekt. Serialiserer ikke objekt til JSON
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SedDataDto(
    var bruker: Bruker, // kaster NullPointerException i Java kode om null
    var kontaktadresse: Adresse? = null,
    var oppholdsadresse: Adresse? = null,
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    var familieMedlem: List<FamilieMedlem> = emptyList(), // kaster NullPointerException i Java kode om null
    var søknadsperiode: Periode? = null,
    var avklartBostedsland: String? = null,
    var vedtakDto: VedtakDto? = null,
    var invalideringSedDto: InvalideringSedDto? = null,
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    var tidligereLovvalgsperioder: List<Lovvalgsperiode> = emptyList(),
    var gsakSaksnummer: Long? = null,
    var mottakerIder: List<String>? = null,
    var ytterligereInformasjon: String? = null,
    var svarAnmodningUnntak: SvarAnmodningUnntakDto? = null,
    var utpekingAvvis: UtpekingAvvisDto? = null,
    var sedType: String? = null,
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    var utenlandskIdent: List<Ident> = emptyList(), // kaster NullPointerException i Java kode om null
    var bostedsadresse: Adresse? = null,
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    var arbeidsgivendeVirksomheter: List<Virksomhet> = emptyList(),
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    var selvstendigeVirksomheter: List<Virksomhet> = emptyList(), // kaster NullPointerException i Java kode om null
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    var arbeidssteder: List<Arbeidssted> = emptyList(), // kaster NullPointerException i Java kode om null
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    var arbeidsland: List<Arbeidsland> = emptyList(), // kaster NullPointerException i Java kode om null
    var harFastArbeidssted: Boolean? = null,
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    var lovvalgsperioder: List<Lovvalgsperiode> = emptyList(),
    var gjeldenderegler: String? = null
) {
    fun finnLovvalgslandDefaultNO(): String = lovvalgsperioder.firstOrNull { it.lovvalgsland != null }?.lovvalgsland ?: "NO"

    fun finnLovvalgsperiode(): Lovvalgsperiode? = lovvalgsperioder.maxByOrNull { it.fom!! }

    fun manglerAdresser(): Boolean =
        bostedsadresse == null && kontaktadresse == null && oppholdsadresse == null
}
