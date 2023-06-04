package no.nav.melosys.eessi.metrikker;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;
import no.nav.melosys.eessi.models.kafkadlq.QueueType;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DLQMetrikkerBinder implements MeterBinder {

    private final DLQMetrikkerCache dlqMetrikkerCache;

    @Override
    public void bindTo(@NotNull MeterRegistry meterRegistry) {
        for (QueueType queueType : QueueType.values()) {
            Gauge.builder(MetrikkerNavn.KAFKA_DLQ_ANTALL, dlqMetrikkerCache, cache -> cache.getQueueSize(queueType))
                .tag("queue_type", queueType.name())
                .register(meterRegistry);
        }
    }
}
