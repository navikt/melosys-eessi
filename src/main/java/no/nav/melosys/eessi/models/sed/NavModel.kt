package no.nav.melosys.eessi.models.sed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include


@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Nav (
        var arbeidssted: List<Arbeidssted>? = null,
        var bruker: Bruker? = null,
        var selvstendig: Selvstendig? = null,
        var ytterligereinformasjon: String? = null,
        var arbeidsgiver: List<Arbeidsgiver>? = null,
        var sak: Sak? = null,
        var eessisak: EessiSak? = null
)

data class Arbeidssted (
        var adresse: Adresse? = null,
        var erikkefastadresse: String? = null,
        var hjemmebase: String? = null,
        var navn: String? = null
)


data class Bruker (
        var adresse: List<Adresse>? = null,
        var far: Forelder? = null,
        var mor: Forelder? = null,
        var person: Person? = null
)

data class Adresse (
    var by: String? = null,
    var bygning: String? = null,
    var gate: String? = null,
    var land: String? = null,
    var postnummer: String? = null,
    var region: String? = null,
    var type: String? = null
)

data class Forelder(
        var person: Person? = null
)

data class Person (
        var etternavn: String? = null,
        var etternavnvedfoedsel: String? = null,
        var foedested: Foedested? = null,
        var foedselsdato: String? = null,
        var fornavn: String? = null,
        var fornavnvedfoedsel: String? = null,
        var kjoenn: String? = null,
        var pin: List<Pin>? = null,
        var statsborgerskap: List<Statsborgerskap>? = null
)

data class Foedested (
    var by: String? = null,
    var land: String? = null,
    var region: String? = null
)

data class Pin (
    var identifikator: String? = null,
    var land: String? = null,
    var sektor: String? = null,
    var institusjonsid: String? = null,
    var institusjonsnavn: String? = null
) {
    constructor(identifikator: String?, land: String?, sektor: String?) : this() {
        this.identifikator = identifikator
        this.land = land
        this.sektor = sektor
    }
}

data class Statsborgerskap (
    var land: String? = null
)

data class Selvstendig (
    var arbeidsgiver: List<Arbeidsgiver>? = null
)

data class Arbeidsgiver (
        var adresse: Adresse? = null,
        var identifikator: List<Identifikator>? = null,
        var navn: String? = null
)

data class Sak (
        var anmodning: X001Anmodning? = null,
        var kontekst: Kontekst? = null
)

data class X001Anmodning (
    var avslutning: Avslutning? = null
)

data class Kontekst (
    var bruker: Bruker? = null
)

data class Avslutning (
        var dato: String? = null,
        var aarsak: Aarsak? = null,
        var type: String? = null
)

data class Aarsak (
    var type: String? = null,
    var annet: String? = null
)

data class EessiSak (

    var institusjonsnummer: String? = null,
    var institusjonsid: String? = null
)