package no.nav.melosys.eessi.models.metrikker

import no.nav.melosys.eessi.models.kafkadlq.QueueType

interface KafkaDLQAntall {
    val queueType: QueueType?
    val antall: Long?
}
