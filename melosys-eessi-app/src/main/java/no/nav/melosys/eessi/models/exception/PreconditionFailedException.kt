package no.nav.melosys.eessi.models.exception

class PreconditionFailedException : RuntimeException {
    constructor(message: String?) : super(message)

    constructor(message: String?, e: Throwable?) : super(message, e)
}
