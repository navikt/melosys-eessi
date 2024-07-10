package no.nav.melosys.eessi.models.exception

class MappingException : RuntimeException {
    constructor(message: String?) : super(message)

    constructor() : super()

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
