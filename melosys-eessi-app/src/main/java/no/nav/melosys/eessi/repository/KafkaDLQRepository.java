package no.nav.melosys.eessi.repository;

import java.util.List;
import java.util.UUID;

import no.nav.melosys.eessi.models.kafkadlq.KafkaDLQ;
import no.nav.melosys.eessi.models.metrikker.KafkaDLQAntall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KafkaDLQRepository extends JpaRepository<KafkaDLQ, UUID> {

    @Query(value = "SELECT dlq.queue_type as queueType, COUNT(*) as antall FROM kafka_dlq dlq GROUP BY dlq.queue_type",
        nativeQuery = true)
    List<KafkaDLQAntall> countDLQByQueueType();
}
