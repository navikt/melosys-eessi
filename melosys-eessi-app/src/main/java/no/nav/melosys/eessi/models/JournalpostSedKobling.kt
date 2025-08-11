package no.nav.melosys.eessi.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "JOURNALPOST_SED_KOBLING")
class JournalpostSedKobling(
    @Id
    @Column(name = "journalpost_id")
    var journalpostID: String,

    @Column(name = "rina_saksnummer", nullable = false)
    var rinaSaksnummer: String,

    @Column(name = "sed_id", nullable = false)
    var sedId: String,

    @Column(name = "sed_versjon", nullable = false)
    var sedVersjon: String,

    @Column(name = "buc_type", nullable = false)
    var bucType: String,

    @Column(name = "sed_type", nullable = false)
    var sedType: String,
) {
    fun erASed(): Boolean {
        return sedType.uppercase().startsWith("A")
    }

    fun erHSed(): Boolean {
        return sedType.uppercase().startsWith("H")
    }
}
