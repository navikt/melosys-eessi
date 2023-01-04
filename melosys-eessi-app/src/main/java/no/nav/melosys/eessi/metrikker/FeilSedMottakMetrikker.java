package no.nav.melosys.eessi.metrikker;

import javax.annotation.PostConstruct;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.repository.SedMottattRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Deprecated(forRemoval = true) //TODO: må erstattes med nye metrikker
public class FeilSedMottakMetrikker {

    private final MeterRegistry meterRegistry;
    private final SedMottattRepository sedMottattRepository;

    public FeilSedMottakMetrikker(MeterRegistry meterRegistry,
            SedMottattRepository sedMottattRepository) {
        this.meterRegistry = meterRegistry;
        this.sedMottattRepository = sedMottattRepository;
    }

    @PostConstruct
    public void initGauge() {
        Gauge.builder(MetrikkerNavn.SED_MOTTO_FEILET_DEPRECATED, this, v -> sedMottattRepository.countByFeiletIsTrue())
                .register(meterRegistry);
    }
}
