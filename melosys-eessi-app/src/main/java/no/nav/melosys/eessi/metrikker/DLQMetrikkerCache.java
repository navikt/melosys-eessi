package no.nav.melosys.eessi.metrikker;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.kafkadlq.QueueType;
import no.nav.melosys.eessi.models.metrikker.KafkaDLQAntall;
import no.nav.melosys.eessi.repository.KafkaDLQRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DLQMetrikkerCache {

    private final Map<QueueType, Long> queueSizeMap = new EnumMap<>(QueueType.class);

    private final KafkaDLQRepository kafkaDLQRepositoy;

    @Scheduled(fixedRate = 30000)
    public void oppfriskDLQTypeOgAntall() {
        log.debug("Oppfrisker antall DLQ per type");
        queueSizeMap.clear();
        List<KafkaDLQAntall> kafkaDLQTypeOgAntall = kafkaDLQRepositoy.countDLQByQueueType();
        for (KafkaDLQAntall count : kafkaDLQTypeOgAntall) {
            queueSizeMap.put(count.getQueueType(), count.getAntall());
        }
    }

    public double getQueueSize(QueueType queueType) {
        return queueSizeMap.getOrDefault(queueType, 0L);
    }
}
