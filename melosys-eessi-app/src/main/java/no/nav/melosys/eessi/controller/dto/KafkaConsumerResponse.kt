package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class KafkaConsumerResponse(
    val consumerId: String? = null,
    val groupId: String? = null,
    val listenerId: String? = null,
    val active: Boolean = false,
    val assignments: List<KafkaConsumerAssignmentResponse>? = null
) {
    class Builder {
        private var consumerId: String? = null
        private var groupId: String? = null
        private var listenerId: String? = null
        private var active: Boolean = false
        private var assignments: List<KafkaConsumerAssignmentResponse>? = null

        fun consumerId(consumerId: String?) = apply { this.consumerId = consumerId }
        fun groupId(groupId: String?) = apply { this.groupId = groupId }
        fun listenerId(listenerId: String?) = apply { this.listenerId = listenerId }
        fun active(active: Boolean) = apply { this.active = active }
        fun assignments(assignments: List<KafkaConsumerAssignmentResponse>?) = apply { this.assignments = assignments }

        fun build() = KafkaConsumerResponse(
            consumerId = consumerId,
            groupId = groupId,
            listenerId = listenerId,
            active = active,
            assignments = assignments
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}
