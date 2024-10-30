package no.nav.melosys.eessi.controller.dto

import no.nav.melosys.eessi.models.sed.nav.Arbeidsgiver
import no.nav.melosys.eessi.models.sed.nav.Identifikator
import no.nav.melosys.eessi.service.sed.helpers.StreamUtils

data class Virksomhet(
    var navn: String? = null,
    var adresse: Adresse? = null,
    var orgnr: String? = null,
    var type: String? = null // Optional based on Java comment
) {
    companion object {
        fun av(arbeidsgiver: Arbeidsgiver): Virksomhet {
            return Virksomhet().apply {
                navn = arbeidsgiver.navn
                adresse = arbeidsgiver.adresse?.let { Adresse.av(it) }
                orgnr = StreamUtils.nullableStream(arbeidsgiver.identifikator)
                    .findFirst()
                    .map(Identifikator::id)
                    .orElse(null)
            }
        }
    }
}
