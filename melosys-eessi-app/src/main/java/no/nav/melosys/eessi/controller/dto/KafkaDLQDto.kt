package no.nav.melosys.eessi.controller.dto

import java.time.LocalDateTime
import no.nav.melosys.eessi.models.kafkadlq.QueueType

data class KafkaDLQDto(
    var id: String? = null,
    var queueType: QueueType? = null,
    var tidRegistrert: LocalDateTime? = null,
    var tidSistRekjort: LocalDateTime? = null,
    var sisteFeilmelding: String? = null,
    var antallRekjoringer: Int = 0,
    var melding: String? = null,
    var skip: Boolean? = null
) {
    class Builder {
        private var id: String? = null
        private var queueType: QueueType? = null
        private var tidRegistrert: LocalDateTime? = null
        private var tidSistRekjort: LocalDateTime? = null
        private var sisteFeilmelding: String? = null
        private var antallRekjoringer: Int = 0
        private var melding: String? = null
        private var skip: Boolean? = null

        fun id(id: String) = apply { this.id = id }
        fun queueType(queueType: QueueType) = apply { this.queueType = queueType }
        fun tidRegistrert(tidRegistrert: LocalDateTime) = apply { this.tidRegistrert = tidRegistrert }
        fun tidSistRekjort(tidSistRekjort: LocalDateTime) = apply { this.tidSistRekjort = tidSistRekjort }
        fun sisteFeilmelding(sisteFeilmelding: String) = apply { this.sisteFeilmelding = sisteFeilmelding }
        fun antallRekjoringer(antallRekjoringer: Int) = apply { this.antallRekjoringer = antallRekjoringer }
        fun melding(melding: String) = apply { this.melding = melding }
        fun skip(skip: Boolean) = apply { this.skip = skip }

        fun build() = KafkaDLQDto(
            id = id,
            queueType = queueType,
            tidRegistrert = tidRegistrert,
            tidSistRekjort = tidSistRekjort,
            sisteFeilmelding = sisteFeilmelding,
            antallRekjoringer = antallRekjoringer,
            melding = melding,
            skip = skip
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}
