package no.nav.melosys.eessi.models

import jakarta.persistence.*
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity(name = "sed_mottatt_hendelse")
@EntityListeners(AuditingEntityListener::class)
class SedMottattHendelse(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = 0,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sed_hendelse", columnDefinition = "jsonb", nullable = false)
    var sedHendelse: SedHendelse,

    @Column(name = "journalpost_id")
    var journalpostId: String? = null,

    @Column(name = "publisert_kafka")
    var publisertKafka: Boolean = false,

    @Column(name = "skal_journalfoeres")
    var skalJournalfoeres: Boolean = true,

    @CreatedDate
    @Column(name = "mottatt_dato")
    var mottattDato: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "endret_dato")
    var sistEndretDato: LocalDateTime? = null
) {
    fun isPublisertKafka() = publisertKafka // mulig vi fjerner denne metoden når vi konverterer klasser som bruker dette til kotlin

    class Builder { // Fjen builder når vi konverterer klasser som bruker dette til kotlin
        private var sedHendelse: SedHendelse? = null
        private var journalpostId: String? = null
        private var publisertKafka: Boolean = false
        private var skalJournalfoeres: Boolean = true
        private var mottattDato: LocalDateTime? = null
        private var sistEndretDato: LocalDateTime? = null

        fun sedHendelse(sedHendelse: SedHendelse?) = apply { this.sedHendelse = sedHendelse }
        fun journalpostId(journalpostId: String?) = apply { this.journalpostId = journalpostId }
        fun publisertKafka(publisertKafka: Boolean) = apply { this.publisertKafka = publisertKafka }
        fun skalJournalfoeres(skalJournalfoeres: Boolean) = apply { this.skalJournalfoeres = skalJournalfoeres }
        fun mottattDato(mottattDato: LocalDateTime?) = apply { this.mottattDato = mottattDato }
        fun sistEndretDato(sistEndretDato: LocalDateTime?) = apply { this.sistEndretDato = sistEndretDato }

        fun build() = SedMottattHendelse(
            0,
            sedHendelse ?: throw IllegalArgumentException("sedHendelse must be set"),
            journalpostId,
            publisertKafka,
            skalJournalfoeres,
            mottattDato,
            sistEndretDato
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}
