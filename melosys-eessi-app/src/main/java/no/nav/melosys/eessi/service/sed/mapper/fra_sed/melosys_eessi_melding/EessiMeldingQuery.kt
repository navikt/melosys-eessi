package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.models.sed.SED

class EessiMeldingQuery(
    val aktoerId: String? = null,
    val sed: SED? = null,
    var rinaDokumentID: String? = null,
    var rinaSaksnummer: String? = null,
    var sedType: String? = null,
    var bucType: String? = null,
    var avsenderID: String? = null,
    var landkode: String? = null,
    var journalpostID: String? = null,
    var dokumentID: String? = null,
    var gsakSaksnummer: String? = null,
    var sedErEndring: Boolean = false,
    var sedVersjon: String? = null,
) {
    class Builder { // Kan fjerne builder når vi har konvert alle som bruker denne til Kotlin
        private var aktoerId: String? = null
        private var sed: SED? = null
        private var rinaDokumentID: String? = null
        private var rinaSaksnummer: String? = null
        private var sedType: String? = null
        private var bucType: String? = null
        private var avsenderID: String? = null
        private var landkode: String? = null
        private var journalpostID: String? = null
        private var dokumentID: String? = null
        private var gsakSaksnummer: String? = null
        private var sedErEndring: Boolean = false
        private var sedVersjon: String? = null

        fun aktoerId(aktoerId: String?) = apply { this.aktoerId = aktoerId }
        fun sed(sed: SED?) = apply { this.sed = sed }
        fun rinaDokumentID(rinaDokumentID: String?) = apply { this.rinaDokumentID = rinaDokumentID }
        fun rinaSaksnummer(rinaSaksnummer: String?) = apply { this.rinaSaksnummer = rinaSaksnummer }
        fun sedType(sedType: String?) = apply { this.sedType = sedType }
        fun bucType(bucType: String?) = apply { this.bucType = bucType }
        fun avsenderID(avsenderID: String?) = apply { this.avsenderID = avsenderID }
        fun landkode(landkode: String?) = apply { this.landkode = landkode }
        fun journalpostID(journalpostID: String?) = apply { this.journalpostID = journalpostID }
        fun dokumentID(dokumentID: String?) = apply { this.dokumentID = dokumentID }
        fun gsakSaksnummer(gsakSaksnummer: String?) = apply { this.gsakSaksnummer = gsakSaksnummer }
        fun sedErEndring(sedErEndring: Boolean) = apply { this.sedErEndring = sedErEndring }
        fun sedVersjon(sedVersjon: String?) = apply { this.sedVersjon = sedVersjon }

        fun build() = EessiMeldingQuery(
            aktoerId,
            sed,
            rinaDokumentID,
            rinaSaksnummer,
            sedType,
            bucType,
            avsenderID,
            landkode,
            journalpostID,
            dokumentID,
            gsakSaksnummer,
            sedErEndring,
            sedVersjon
        )
    }
}
