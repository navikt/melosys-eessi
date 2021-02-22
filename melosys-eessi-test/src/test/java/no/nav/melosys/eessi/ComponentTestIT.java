package no.nav.melosys.eessi;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.melosys.utils.ConsumerRecordPredicates;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentTestIT extends ComponentTestBase {

    @Test
    void testHappyCase() throws Exception {
        // Venter på to Kafka-meldinger: den vi selv legger på topic som input, og den som kommer som output
        kafkaTestConsumer.reset(2);
        kafkaTemplate.send(createProducerRecord()).get();
        kafkaTestConsumer.doWait(3_000L);

        List<ConsumerRecord<Object, Object>> outputList = kafkaTestConsumer.getRecords().stream().filter(ConsumerRecordPredicates.topic("privat-melosys-eessi-v1-local")).collect(Collectors.toList());
        assertThat(outputList).hasSize(1);
        assertThat(outputList.get(0).value().toString()).contains("2019-06-01");
        assertThat(outputList.get(0).value().toString()).contains("2019-12-01");
    }
}
