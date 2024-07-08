package no.nav.melosys.eessi.models.sed.nav

import no.nav.melosys.eessi.models.person.Kjønn

enum class Kjønn {
    M, K, U;

    fun tilDomene(): Kjønn = when (this) {
        K -> Kjønn.KVINNE
        M -> Kjønn.MANN
        U -> Kjønn.UKJENT
    }
}
