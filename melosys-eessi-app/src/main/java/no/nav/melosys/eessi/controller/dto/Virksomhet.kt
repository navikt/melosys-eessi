package no.nav.melosys.eessi.controller.dto

import no.nav.melosys.eessi.models.sed.nav.Arbeidsgiver
import no.nav.melosys.eessi.models.sed.nav.Identifikator
import java.util.stream.Stream

data class Virksomhet(
    var navn: String? = null,
    var adresse: Adresse,//? = null, // Kaster NullPointereException i java kode
    var orgnr: String? = null,
    var type: String? = null //Trenger kanskje ikke denne?
) {
    companion object {
        fun av(arbeidsgiver: Arbeidsgiver): Virksomhet {
            return Virksomhet(
                navn = arbeidsgiver.navn,
                adresse = arbeidsgiver.adresse.let { Adresse.av(it) },
                orgnr = nullableStream(arbeidsgiver.identifikator)
                    .findFirst()
                    .map(Identifikator::id)
                    .orElse(null)
            )
        }

        fun <T> nullableStream(collection: Collection<T>?): Stream<T> = collection?.stream() ?: Stream.empty()
    }
}
