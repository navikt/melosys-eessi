package no.nav.melosys.eessi.service.sed.helpers;

import java.util.Collection;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StreamUtils {
    public <T> Stream<T> nullableStream(Collection<T> collection) {
        return Stream.ofNullable(collection).flatMap(Collection::stream);
    }
}
