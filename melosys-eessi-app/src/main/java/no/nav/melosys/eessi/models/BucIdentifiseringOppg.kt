package no.nav.melosys.eessi.models

import jakarta.persistence.*

@Entity(name = "buc_identifisering_oppg")
class BucIdentifiseringOppg(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    @Column(name = "rina_saksnummer", nullable = false)
    var rinaSaksnummer: String,

    @Column(name = "oppgave_id", nullable = false)
    var oppgaveId: String,

    @Column(name = "versjon")
    var versjon: Int = 0
) {
    override fun toString(): String =
        "BucIdentifiseringOppg(id=$id, rinaSaksnummer='$rinaSaksnummer', oppgaveId='$oppgaveId', versjon=$versjon)"

    // TODO: fjern builder når vi har ting i kotlin
    class BucIdentifiseringOppgBuilder {
        private var rinaSaksnummer: String? = null
        private var oppgaveId: String? = null
        private var versjon: Int = 0

        fun rinaSaksnummer(rinaSaksnummer: String?) = apply { this.rinaSaksnummer = rinaSaksnummer }
        fun oppgaveId(oppgaveId: String?) = apply { this.oppgaveId = oppgaveId }
        fun versjon(versjon: Int) = apply { this.versjon = versjon }

        fun build() = BucIdentifiseringOppg(
            rinaSaksnummer = rinaSaksnummer ?: throw IllegalArgumentException("rinaSaksnummer kan ikke være null"),
            oppgaveId = oppgaveId ?: throw IllegalArgumentException("oppgaveId kan ikke være null"),
            versjon = versjon
        )

        override fun toString(): String =
            "BucIdentifiseringOppgBuilder(rinaSaksnummer=$rinaSaksnummer, oppgaveId=$oppgaveId, versjon=$versjon)"
    }

    companion object {
        @JvmStatic
        fun builder(): BucIdentifiseringOppgBuilder = BucIdentifiseringOppgBuilder()
    }
}
