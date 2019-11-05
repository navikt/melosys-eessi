package no.nav.melosys.eessi.metrikker;

import javax.annotation.PostConstruct;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.repository.SedMottattRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
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
        Gauge.builder(MetrikkerNavn.SED_MOTTATT_FEILET, this, v -> sedMottattRepository.countByFeiletIsTrue())
                .register(meterRegistry);
    }
}
