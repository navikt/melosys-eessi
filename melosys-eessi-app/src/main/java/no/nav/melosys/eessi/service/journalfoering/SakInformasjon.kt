package no.nav.melosys.eessi.service.journalfoering

data class SakInformasjon(
    var journalpostId: String? = null,
    var dokumentId: String? = null,
    var gsakSaksnummer: String? = null
) {
    data class SakInformasjonBuilder(
        var journalpostId: String? = null,
        var dokumentId: String? = null,
        var gsakSaksnummer: String? = null
    ) {
        fun journalpostId(journalpostId: String?) = apply { this.journalpostId = journalpostId }
        fun dokumentId(dokumentId: String?) = apply { this.dokumentId = dokumentId }
        fun gsakSaksnummer(gsakSaksnummer: String?) = apply { this.gsakSaksnummer = gsakSaksnummer }
        fun build() = SakInformasjon(journalpostId, dokumentId, gsakSaksnummer)

        override fun toString() = "SakInformasjon.SakInformasjonBuilder(journalpostId=$journalpostId, dokumentId=$dokumentId, gsakSaksnummer=$gsakSaksnummer)"
    }

    companion object {
        @JvmStatic
        fun builder() = SakInformasjonBuilder()
    }
}
