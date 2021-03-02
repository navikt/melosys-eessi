package no.nav.melosys.eessi.metrikker;

import java.util.EnumMap;
import java.util.Map;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.service.identifisering.PersonSokResultat;
import no.nav.melosys.eessi.service.identifisering.SoekBegrunnelse;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.metrikker.MetrikkerNavn.*;
import static no.nav.melosys.eessi.metrikker.PersonSokMetrikker.PdlTpsSammenligningResultat.*;

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

    private static final String SØK_SAMMENLIGNING_TREFF_PDL = "pdl";
    private static final String SØK_SAMMENLIGNING_TREFF_TPS = "tps";
    private static final String SØK_SAMMENLIGNING_TREFF_I_BEGGE = "begge";
    private static final String SØK_SAMMENLIGNING_INGEN_TREFF = "ingen";

    private static final Map<SoekBegrunnelse, Counter> SØKBEGRUNNELSE_TELLERE = new EnumMap<>(SoekBegrunnelse.class);
    private static final Map<PdlTpsSammenligningResultat, Counter> PDL_TPS_SAMMENLIGNING_RESULTAT_TELLERE = new EnumMap<>(PdlTpsSammenligningResultat.class);

    private final DistributionSummary pdlSøketreff;
    private final DistributionSummary tpsSøketreff;

    static {
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.IDENTIFISERT, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, IDENTIFISERT));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.FEIL_FOEDSELSDATO, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, ETT_TREFF_FEIL_DATO));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.FLERE_TREFF, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, FLERE_TREFF));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.INGEN_TREFF, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, INGEN_TREFF));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.FEIL_STATSBORGERSKAP, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, ETT_TREFF_FEIL_STATSBORGERSKAP));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.PERSON_OPPHORT, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, ETT_TREFF_PERSON_OPPHORT));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.FNR_IKKE_FUNNET, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, FNR_IKKE_FUNNET));

        PDL_TPS_SAMMENLIGNING_RESULTAT_TELLERE.put(TREFF_PDL, Metrics.counter(PERSONSØK_SAMMENLIGNING, KEY_RESULTAT, SØK_SAMMENLIGNING_TREFF_PDL));
        PDL_TPS_SAMMENLIGNING_RESULTAT_TELLERE.put(TREFF_TPS, Metrics.counter(PERSONSØK_SAMMENLIGNING, KEY_RESULTAT, SØK_SAMMENLIGNING_TREFF_TPS));
        PDL_TPS_SAMMENLIGNING_RESULTAT_TELLERE.put(TREFF_I_BEGGE, Metrics.counter(PERSONSØK_SAMMENLIGNING, KEY_RESULTAT, SØK_SAMMENLIGNING_TREFF_I_BEGGE));
        PDL_TPS_SAMMENLIGNING_RESULTAT_TELLERE.put(INGEN_TREFF_I_BEGGE, Metrics.counter(PERSONSØK_SAMMENLIGNING, KEY_RESULTAT, SØK_SAMMENLIGNING_INGEN_TREFF));
    }

    public PersonSokMetrikker(MeterRegistry meterRegistry) {
        pdlSøketreff = DistributionSummary.builder(PERSONSOK_ANTALL_PDL).baseUnit("short").register(meterRegistry);
        tpsSøketreff = DistributionSummary.builder(PERSONSOK_ANTALL_TPS).baseUnit("short").register(meterRegistry);
    }

    public void counter(final SoekBegrunnelse soekBegrunnelse) {
        Counter counter = SØKBEGRUNNELSE_TELLERE.get(soekBegrunnelse);
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

    public void registrerSammenligningPdlTps(PersonSokResultat tpsResultat, PersonSokResultat pdlResultat) {
        log.info("Resultat personsøk tps: {}, pdl: {}", tpsResultat.getBegrunnelse(), pdlResultat.getBegrunnelse());
        if (tpsResultat.personIdentifisert() && pdlResultat.personIdentifisert()) {
            PDL_TPS_SAMMENLIGNING_RESULTAT_TELLERE.get(TREFF_I_BEGGE).increment();
        } else if (tpsResultat.personIdentifisert()) {
            PDL_TPS_SAMMENLIGNING_RESULTAT_TELLERE.get(TREFF_TPS).increment();
        } else if (pdlResultat.personIdentifisert()) {
            PDL_TPS_SAMMENLIGNING_RESULTAT_TELLERE.get(TREFF_PDL).increment();
        } else {
            PDL_TPS_SAMMENLIGNING_RESULTAT_TELLERE.get(INGEN_TREFF_I_BEGGE).increment();
        }
    }

    enum PdlTpsSammenligningResultat {
        TREFF_PDL,
        TREFF_TPS,
        TREFF_I_BEGGE,
        INGEN_TREFF_I_BEGGE
    }
}
