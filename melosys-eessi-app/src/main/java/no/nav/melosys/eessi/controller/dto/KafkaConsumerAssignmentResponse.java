package no.nav.melosys.eessi.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KafkaConsumerAssignmentResponse {
    private final String topic;
    private final Integer partition;
}
