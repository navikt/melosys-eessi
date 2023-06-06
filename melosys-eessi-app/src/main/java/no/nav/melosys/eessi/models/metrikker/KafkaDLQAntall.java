package no.nav.melosys.eessi.models.metrikker;

import no.nav.melosys.eessi.models.kafkadlq.QueueType;

public interface KafkaDLQAntall {

    QueueType getQueueType();

    Long getAntall();
}
