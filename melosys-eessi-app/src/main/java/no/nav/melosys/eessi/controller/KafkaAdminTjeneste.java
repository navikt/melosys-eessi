package no.nav.melosys.eessi.controller;

import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.KafkaConsumerAssignmentResponse;
import no.nav.melosys.eessi.controller.dto.KafkaConsumerResponse;
import no.nav.melosys.eessi.identifisering.OppgaveEndretConsumer;
import no.nav.melosys.eessi.kafka.consumers.SedMottattConsumer;
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
@RequestMapping("/admin/kafka/consumers")
public class KafkaAdminTjeneste {

    private final static String API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY";

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private final OppgaveEndretConsumer oppgaveEndretConsumer;
    private final SedMottattConsumer sedMottattConsumer;
    private final String apiKey;

    public KafkaAdminTjeneste(OppgaveEndretConsumer oppgaveEndretConsumer,
                              SedMottattConsumer sedMottattConsumer,
                              @Value("${melosys.admin.api-key}") String apiKey) {
        this.oppgaveEndretConsumer = oppgaveEndretConsumer;
        this.sedMottattConsumer = sedMottattConsumer;
        this.apiKey = apiKey;
    }

    @GetMapping()
    public ResponseEntity<List<KafkaConsumerResponse>> hentConsumerIds(@RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);

        return ResponseEntity.ok(kafkaListenerEndpointRegistry.getListenerContainerIds()
            .stream()
            .map(this::lagKafkaConsumerResponseVedId)
            .toList());
    }

    @PostMapping("/{consumerId}/stop")
    public ResponseEntity<KafkaConsumerResponse> stoppKafkaConsumer(@PathVariable String consumerId, @RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);

        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(consumerId);

        if (listenerContainer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        listenerContainer.stop();

        log.info("[KafkaAdminTjeneste] Stoppet consumerId {}", consumerId);

        return ResponseEntity.ok(lagKafkaConsumerResponse(listenerContainer));
    }

    @PostMapping("/{consumerId}/start")
    public ResponseEntity<KafkaConsumerResponse> startKafkaConsumer(@PathVariable String consumerId, @RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);

        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(consumerId);

        if (listenerContainer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        listenerContainer.start();

        log.info("[KafkaAdminTjeneste] Startet consumerId {}", consumerId);

        return ResponseEntity.ok(lagKafkaConsumerResponse(listenerContainer));
    }

    @PostMapping("/{consumerId}/seek/{offset}")
    public ResponseEntity<String> settOffset(@PathVariable String consumerId, @PathVariable long offset, @RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);

        if (!List.of("oppgaveEndret", "sedMottatt").contains(consumerId)) {
            return ResponseEntity.badRequest().body("ConsumerId is not supported: " + consumerId);
        }

        log.info("[KafkaAdminTjeneste] Setter offset for " + consumerId + " til: {}", offset);
        switch (consumerId) {
            case "oppgaveEndret" -> oppgaveEndretConsumer.settSpesifiktOffsetPåConsumer(offset);
            case "sedMottatt" -> sedMottattConsumer.settSpesifiktOffsetPåConsumer(offset);
            default -> log.warn("Vi støtter ikke {}, gjør ingenting.", consumerId);
        }

        return ResponseEntity.ok().build();
    }


    private KafkaConsumerResponse lagKafkaConsumerResponse(MessageListenerContainer listenerContainer) {
        return KafkaConsumerResponse.builder()
            .consumerId(listenerContainer.getListenerId())
            .groupId(listenerContainer.getGroupId())
            .listenerId(listenerContainer.getListenerId())
            .active(listenerContainer.isRunning())
            .assignments(Optional.ofNullable(listenerContainer.getAssignedPartitions())
                .map(topicPartitions -> topicPartitions.stream()
                    .map(this::lagKafkaConsumerAssignmentResponse)
                    .toList())
                .orElse(null))
            .build();
    }

    private KafkaConsumerResponse lagKafkaConsumerResponseVedId(String consumerId) {
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(consumerId);
        return lagKafkaConsumerResponse(listenerContainer);
    }

    private KafkaConsumerAssignmentResponse lagKafkaConsumerAssignmentResponse(TopicPartition topicPartition) {
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
