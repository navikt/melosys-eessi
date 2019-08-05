package no.nav.melosys.utils;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaTestConsumer extends LatchService {
    @Getter
    private final List<ConsumerRecord<Object, Object>> records = new LinkedList<>();

    @KafkaListener(topicPattern = "${person.utils.kafka.test.topicsPattern:.*}", groupId = "test", containerFactory = "testKafkaListenerContainerFactory")
    void handle(ConsumerRecord<Object, Object> mess) {
        log.info("Read message from topic: " + mess.topic());
        records.add(mess);
        countDown();
    }

    @Override
    public void reset(int count) {
        records.clear();
        super.reset(count);
    }
}