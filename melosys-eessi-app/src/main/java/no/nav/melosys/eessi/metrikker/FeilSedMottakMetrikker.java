package no.nav.melosys.eessi.metrikker;

import io.micrometer.core.instrument.Gauge;
import no.nav.melosys.eessi.repository.SedMottattRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class FeilSedMottakMetrikker {

    private final SedMottattRepository sedMottattRepository;

    public FeilSedMottakMetrikker(SedMottattRepository sedMottattRepository) {
        this.sedMottattRepository = sedMottattRepository;
    }

    @PostConstruct
    public void initGauge() {
        Gauge.builder("test", this, v -> sedMottattRepository.findAllByFerdigFalseAndFeiletFalse().size());
    }
}
