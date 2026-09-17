package no.nav.melosys.eessi.models.exception

/**
 * Kastes når eux-rina-api faktisk har mottatt og behandlet en forespørsel, men avvist den
 * (f.eks. en valideringsfeil i SED-et som "The action requires a valid SED and it is not.").
 * Et nytt forsøk med samme data vil alltid gi samme resultat.
 *
 * Skiller seg fra [IntegrationException] ved at konsumenter (som melosys-api sin EessiClient)
 * kan la være å retry'e denne feilen, i motsetning til forbigående feil der vi ikke fikk noe
 * svar fra eux-rina-api i det hele tatt (f.eks. timeout/nettverksfeil).
 */
class IkkeRetrybarIntegrationException : IntegrationException {
    constructor(message: String?) : super(message)

    constructor(message: String?, e: Throwable?) : super(message, e)
}
