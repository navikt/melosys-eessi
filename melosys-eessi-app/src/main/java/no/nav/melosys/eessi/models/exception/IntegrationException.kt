package no.nav.melosys.eessi.models.exception

class IntegrationException : RuntimeException {
    constructor(message: String?) : super(message)

    constructor(message: String?, e: Throwable?) : super(message, e)
}
