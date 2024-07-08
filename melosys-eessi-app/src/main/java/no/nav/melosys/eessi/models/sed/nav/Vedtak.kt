package no.nav.melosys.eessi.models.sed.nav

abstract class Vedtak {
    var datoforrigevedtak: String? = null
    var eropprinneligvedtak: String? = null // RINA regler: Kan bare sette "ja" eller null (default: null, som betyr nei)
    var erendringsvedtak: String? = null // RINA regler: Kan bare sette "nei" eller null (default: null, som betyr ja)
}
