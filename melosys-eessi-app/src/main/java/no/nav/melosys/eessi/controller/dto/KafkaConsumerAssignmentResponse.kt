package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class KafkaConsumerAssignmentResponse(
    val topic: String? = null,
    val partition: Int? = null
) {
    class Builder {
        private var topic: String? = null
        private var partition: Int? = null

        fun topic(topic: String?) = apply { this.topic = topic }
        fun partition(partition: Int?) = apply { this.partition = partition }

        fun build() = KafkaConsumerAssignmentResponse(
            topic = topic,
            partition = partition
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}
