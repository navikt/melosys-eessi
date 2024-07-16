package no.nav.melosys.eessi.service.eux

data class BucSearch(
    var fnr: String? = null,
    var fornavn: String? = null,
    var etternavn: String? = null,
    var foedselsdato: String? = null,
    var rinaSaksnummer: String? = null,
    var bucType: String? = null,
    var status: String? = null
) {
    companion object {
        @JvmStatic
        fun builder() = BucSearchBuilder()
    }

    data class BucSearchBuilder(
        private var fnr: String? = null,
        private var fornavn: String? = null,
        private var etternavn: String? = null,
        private var foedselsdato: String? = null,
        private var rinaSaksnummer: String? = null,
        private var bucType: String? = null,
        private var status: String? = null
    ) {
        fun fnr(fnr: String) = apply { this.fnr = fnr }
        fun fornavn(fornavn: String) = apply { this.fornavn = fornavn }
        fun etternavn(etternavn: String) = apply { this.etternavn = etternavn }
        fun foedselsdato(foedselsdato: String) = apply { this.foedselsdato = foedselsdato }
        fun rinaSaksnummer(rinaSaksnummer: String) = apply { this.rinaSaksnummer = rinaSaksnummer }
        fun bucType(bucType: String) = apply { this.bucType = bucType }
        fun status(status: String) = apply { this.status = status }

        fun build() = BucSearch(fnr, fornavn, etternavn, foedselsdato, rinaSaksnummer, bucType, status)
    }
}
