package no.nav.melosys.eessi.kafka.producers.model

import org.apache.commons.lang3.StringUtils

data class Arbeidssted(
    var navn: String?,
    var adresse: Adresse,
    var hjemmebase: String?,
    var isErIkkeFastAdresse: Boolean
) {
    constructor(arbeidssted: no.nav.melosys.eessi.models.sed.nav.Arbeidssted) : this(
        arbeidssted.navn,
        Adresse(arbeidssted.adresse!!),
        arbeidssted.hjemmebase,
        StringUtils.equals(arbeidssted.erikkefastadresse, "1")
    )
}
