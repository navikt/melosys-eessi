package no.nav.melosys.eessi.models.kafkadlq

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "queue_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "kafka_dlq")
abstract class KafkaDLQ(
    @Id
    @Column(name = "id")
    var id: UUID? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "queue_type", insertable = false, updatable = false)
    var queueType: QueueType? = null,

    @Column(name = "tid_registrert")
    var tidRegistrert: LocalDateTime = LocalDateTime.now(),

    @Column(name = "tid_sist_rekjort")
    var tidSistRekjort: LocalDateTime? = null,

    @Column(name = "siste_feilmelding")
    var sisteFeilmelding: String? = null,

    @Column(name = "antall_rekjoringer")
    var antallRekjoringer: Int = 0,

    @Column(name = "skip")
    var skip: Boolean = false
) {

    fun økAntallRekjøringerMed1() {
        antallRekjoringer++
    }

    abstract fun hentMeldingSomStreng(): String
}
