package no.nav.melosys.eessi;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.melosys.utils.ConsumerRecordPredicates;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentTest extends ComponentTestBase {

    @Test
    public void testHappyCase() throws Exception {
        kafkaTestConsumer.reset(2);
        kafkaTemplate.send(createProducerRecord()).get();
        kafkaTestConsumer.doWait(10_000L);

        List<ConsumerRecord<Object, Object>> outputList = kafkaTestConsumer.getRecords().stream().filter(ConsumerRecordPredicates.topic("privat-melosys-eessi-v1-local")).collect(Collectors.toList());
        assertThat(outputList).hasSize(1);
        assertThat(outputList.get(0).value().toString()).contains("2019-06-01");
        assertThat(outputList.get(0).value().toString()).contains("2019-12-01");
    }
}
