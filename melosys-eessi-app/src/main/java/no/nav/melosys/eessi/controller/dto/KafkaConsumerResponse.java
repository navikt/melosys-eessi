package no.nav.melosys.eessi.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KafkaConsumerResponse {
    private final String consumerId;
    private final String groupId;
    private final String listenerId;
    private final boolean active;

    private final List<KafkaConsumerAssignmentResponse> assignments;
}
