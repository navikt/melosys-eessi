package no.nav.melosys.utils;

import java.util.LinkedList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaTestConsumer extends LatchService {
    private final List<ConsumerRecord<Object, String>> records = new LinkedList<>();

    public List<ConsumerRecord<Object, String>> getRecords() {
        return records;
    }

    @KafkaListener(topicPattern = ".*", groupId = "test", containerFactory = "testKafkaListenerContainerFactory")
    void handle(ConsumerRecord<Object, String> mess) {
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
