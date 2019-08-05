package no.nav.melosys.utils;

import java.util.function.Predicate;

import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import static no.nav.melosys.utils.HeaderUtils.getAllHeadersByKeyAsStrings;

@UtilityClass
public class ConsumerRecordPredicates {

    public static Predicate<ConsumerRecord> topic(String topic) {
        return record -> record.topic().equals(topic);
    }

    public static Predicate<ConsumerRecord> key(String key) {
        return record -> record.key().equals(key);
    }

    public static Predicate<ConsumerRecord> valueType(Class<?> type) {
        return record -> record.value() != null && record.value().getClass().isAssignableFrom(type);
    }

    public static Predicate<ConsumerRecord> header(String key, String value) {
        return record -> getAllHeadersByKeyAsStrings(record.headers(), key).stream().anyMatch(e -> e.equals(value));
    }
}