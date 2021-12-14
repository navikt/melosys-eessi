package no.nav.melosys.eessi.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.KafkaConsumerAssignmentResponse;
import no.nav.melosys.eessi.controller.dto.KafkaConsumerResponse;
import no.nav.security.token.support.core.api.Unprotected;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Unprotected
@RestController
@RequestMapping("/admin/kafka")
public class KafkaAdminTjeneste {

    private final static String API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY";

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private final String apiKey;

    public KafkaAdminTjeneste(@Value("${melosys.admin.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @GetMapping("/consumers")
    public ResponseEntity<List<KafkaConsumerResponse>> getConsumerIds(@RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);

        return ResponseEntity.ok(kafkaListenerEndpointRegistry.getListenerContainerIds()
            .stream()
            .map(this::createKafkaConsumerResponseById)
            .collect(Collectors.toList()));
    }

    @PostMapping("consumers/{consumerId}/stop")
    public ResponseEntity<KafkaConsumerResponse> stopKafkaConsumer(@PathVariable String consumerId, @RequestHeader(API_KEY_HEADER) String apiKey){
        validerApikey(apiKey);

        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(consumerId);

        if(listenerContainer == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        listenerContainer.stop();

        return ResponseEntity.ok(createKafkaConsumerResponse(listenerContainer));
    }

    @PostMapping("consumers/{consumerId}/start")
    public ResponseEntity<KafkaConsumerResponse> startKafkaConsumer(@PathVariable String consumerId, @RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);

        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(consumerId);

        if(listenerContainer == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        listenerContainer.start();

        return ResponseEntity.ok(createKafkaConsumerResponse(listenerContainer));
    }

    private KafkaConsumerResponse createKafkaConsumerResponse(MessageListenerContainer listenerContainer){
        return KafkaConsumerResponse.builder()
            .consumerId(listenerContainer.getListenerId())
            .groupId(listenerContainer.getGroupId())
            .listenerId(listenerContainer.getListenerId())
            .active(listenerContainer.isRunning())
            .assignments(Optional.ofNullable(listenerContainer.getAssignedPartitions())
                .map(topicPartitions -> topicPartitions.stream()
                    .map(this::createKafkaConsumerAssignmentResponse)
                    .collect(Collectors.toList()))
                .orElse(null))
            .build();
    }

    private KafkaConsumerResponse createKafkaConsumerResponseById(String consumerId) {
        MessageListenerContainer listenerContainer =
            kafkaListenerEndpointRegistry.getListenerContainer(consumerId);
        return createKafkaConsumerResponse(listenerContainer);
    }

    private KafkaConsumerAssignmentResponse createKafkaConsumerAssignmentResponse(
        TopicPartition topicPartition) {
        return KafkaConsumerAssignmentResponse.builder()
            .topic(topicPartition.topic())
            .partition(topicPartition.partition())
            .build();
    }

    private void validerApikey(String value) {
        if (!apiKey.equals(value)) {
            throw new SecurityException("Trenger gyldig apikey");
        }
    }
}
