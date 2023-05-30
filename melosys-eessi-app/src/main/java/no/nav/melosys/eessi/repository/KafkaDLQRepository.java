package no.nav.melosys.eessi.repository;

import java.util.UUID;

import no.nav.melosys.eessi.models.kafkadlq.KafkaDLQ;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KafkaDLQRepository extends JpaRepository<KafkaDLQ, UUID> {
}
