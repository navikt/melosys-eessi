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
    var familieMedlem: List<FamilieMedlem>, // kaster NullPointerException i Java kode om null
    var søknadsperiode: Periode? = null,
    var avklartBostedsland: String? = null,
    var vedtakDto: VedtakDto? = null,
    var invalideringSedDto: InvalideringSedDto? = null,
    var tidligereLovvalgsperioder: List<Lovvalgsperiode>? = null,
    var gsakSaksnummer: Long? = null,
    var mottakerIder: List<String>? = null,
    override var ytterligereInformasjon: String? = null,
    var svarAnmodningUnntak: SvarAnmodningUnntakDto? = null,
    var utpekingAvvis: UtpekingAvvisDto? = null
) : SedGrunnlagDto() {

    fun finnLovvalgslandDefaultNO(): String = (lovvalgsperioder ?: throw NullPointerException("lovvalgsperioder kan ikke være null"))
        .firstOrNull { it.lovvalgsland != null }?.lovvalgsland ?: "NO"

    fun finnLovvalgsperiode(): Lovvalgsperiode? = lovvalgsperioder?.maxByOrNull { it.fom!! }

    fun manglerAdresser(): Boolean =
        bostedsadresse == null && kontaktadresse == null && oppholdsadresse == null
}
