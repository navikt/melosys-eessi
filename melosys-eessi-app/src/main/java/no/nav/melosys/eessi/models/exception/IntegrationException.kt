package no.nav.melosys.eessi.models.exception

open class IntegrationException : RuntimeException {
    constructor(message: String?) : super(message)

    constructor(message: String?, e: Throwable?) : super(message, e)
}
