package no.nav.melosys.eessi.service.sed.helpers

import java.util.stream.Stream

object StreamUtils {
    @JvmStatic
    fun <T> nullableStream(collection: Collection<T>?): Stream<T> {
        return collection?.stream() ?: Stream.empty()
    }
}
