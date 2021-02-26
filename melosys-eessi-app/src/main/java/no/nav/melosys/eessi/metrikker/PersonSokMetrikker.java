package no.nav.melosys.eessi.metrikker;

import java.util.EnumMap;
import java.util.Map;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.service.identifisering.SoekBegrunnelse;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.metrikker.MetrikkerNavn.IDENTIFISERING;
import static no.nav.melosys.eessi.metrikker.MetrikkerNavn.KEY_RESULTAT;

@Slf4j
@Component
public class PersonSokMetrikker {

    private static final String IDENTIFISERT = "identifisert";
    private static final String INGEN_TREFF = "nullTreff";
    private static final String FLERE_TREFF = "flereTreff";
    private static final String ETT_TREFF_FEIL_DATO = "feilFoedselsdato";
    private static final String ETT_TREFF_FEIL_STATSBORGERSKAP = "feilStatsborgerskap";
    private static final String ETT_TREFF_PERSON_OPPHORT = "personOpphort";
    private static final String FNR_IKKE_FUNNET = "fnrIkkeFunnet";

    private static final Map<SoekBegrunnelse, Counter> tellere = new EnumMap<>(SoekBegrunnelse.class);

    private final DistributionSummary pdlSøketreff;
    private final DistributionSummary tpsSøketreff;

    static {
        tellere.put(SoekBegrunnelse.IDENTIFISERT, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, IDENTIFISERT));
        tellere.put(SoekBegrunnelse.FEIL_FOEDSELSDATO, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, ETT_TREFF_FEIL_DATO));
        tellere.put(SoekBegrunnelse.FLERE_TREFF, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, FLERE_TREFF));
        tellere.put(SoekBegrunnelse.INGEN_TREFF, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, INGEN_TREFF));
        tellere.put(SoekBegrunnelse.FEIL_STATSBORGERSKAP, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, ETT_TREFF_FEIL_STATSBORGERSKAP));
        tellere.put(SoekBegrunnelse.PERSON_OPPHORT, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, ETT_TREFF_PERSON_OPPHORT));
        tellere.put(SoekBegrunnelse.FNR_IKKE_FUNNET, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, FNR_IKKE_FUNNET));
    }

    public PersonSokMetrikker(MeterRegistry meterRegistry) {
        pdlSøketreff = DistributionSummary.builder("personsok.antall.pdl").baseUnit("short").register(meterRegistry);
        tpsSøketreff = DistributionSummary.builder("personsok.antall.tps").baseUnit("short").register(meterRegistry);
    }

    public void counter(final SoekBegrunnelse soekBegrunnelse) {
        Counter counter = tellere.get(soekBegrunnelse);
        if (counter != null) {
            counter.increment();
        } else {
            log.warn("Kunne ikke finne teller for søkbegrunnelse {}", soekBegrunnelse);
        }
    }

    public void registrerAntallTreffPDL(int antallTreff) {
        pdlSøketreff.record(antallTreff);
    }

    public void registrerAntallTreffTps(int antallTreff) {
        tpsSøketreff.record(antallTreff);
    }
}
