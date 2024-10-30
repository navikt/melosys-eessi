package no.nav.melosys.eessi.controller.dto

data class BucOgSedOpprettetDto(
    var rinaSaksnummer: String? = null,
    var rinaUrl: String? = null
) {
    class Builder {
        private var rinaSaksnummer: String? = null
        private var rinaUrl: String? = null

        fun rinaSaksnummer(rinaSaksnummer: String?) = apply { this.rinaSaksnummer = rinaSaksnummer }
        fun rinaUrl(rinaUrl: String?) = apply { this.rinaUrl = rinaUrl }

        fun build() = BucOgSedOpprettetDto(
            rinaSaksnummer = rinaSaksnummer,
            rinaUrl = rinaUrl
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}
