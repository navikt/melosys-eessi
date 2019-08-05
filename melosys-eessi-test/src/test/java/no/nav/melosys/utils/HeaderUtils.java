package no.nav.melosys.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import lombok.experimental.UtilityClass;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.*;

@UtilityClass
public class HeaderUtils {

    public static Map<String, List<String>> toMap(Headers headers) {
        return StreamSupport
                .stream(headers.spliterator(), false)
                .collect(groupingBy(Header::key, mapping(h -> new String(h.value(), UTF_8), toList())));
    }

    public static byte[] toUtf8Bytes(Object object) {
        if (object != null) {
            return object.toString().getBytes(UTF_8);
        }
        return new byte[0];
    }

    public static String getLastHeaderByKeyAsString(Headers headers, String key, String defaultValue) {
        Header header = headers.lastHeader(key);
        if (header != null && header.value() != null) {
            return new String(header.value(), UTF_8);
        }
        return defaultValue;
    }

    public static List<String> getAllHeadersByKeyAsStrings(Headers headers, String key) {
        return StreamSupport.stream(headers.spliterator(), false)
                .filter(e -> e.key().equals(key))
                .filter(e -> e.value() != null)
                .map(e -> new String(e.value(), UTF_8))
                .collect(toList());
    }
}