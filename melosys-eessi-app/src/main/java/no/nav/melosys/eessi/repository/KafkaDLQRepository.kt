package no.nav.melosys.eessi.repository

import no.nav.melosys.eessi.models.kafkadlq.KafkaDLQ
import no.nav.melosys.eessi.models.metrikker.KafkaDLQAntall
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface KafkaDLQRepository : JpaRepository<KafkaDLQ, UUID> {
    @Query(value = "SELECT dlq.queue_type as queueType, COUNT(*) as antall FROM kafka_dlq dlq GROUP BY dlq.queue_type", nativeQuery = true)
    fun countDLQByQueueType(): MutableList<KafkaDLQAntall>
}
