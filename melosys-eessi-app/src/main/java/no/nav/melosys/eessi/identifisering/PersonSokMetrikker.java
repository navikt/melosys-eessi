package no.nav.melosys.eessi.identifisering;

import java.util.EnumMap;
import java.util.Map;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.metrikker.MetrikkerNavn.METRIKKER_NAMESPACE;

@Slf4j
@Component
class PersonSokMetrikker {

    private static final String IDENTIFISERING = METRIKKER_NAMESPACE + "identifisering";
    private static final String KEY_RESULTAT = "resultat";

    private static final String IDENTIFISERT = "identifisert";
    private static final String INGEN_TREFF = "nullTreff";
    private static final String FLERE_TREFF = "flereTreff";
    private static final String ETT_TREFF_FEIL_DATO = "feilFoedselsdato";
    private static final String ETT_TREFF_FEIL_STATSBORGERSKAP = "feilStatsborgerskap";
    private static final String ETT_TREFF_PERSON_OPPHORT = "personOpphort";
    private static final String FNR_IKKE_FUNNET = "fnrIkkeFunnet";

    private static final Map<SoekBegrunnelse, Counter> SØKBEGRUNNELSE_TELLERE = new EnumMap<>(SoekBegrunnelse.class);

    static {
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.IDENTIFISERT, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, IDENTIFISERT));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.FEIL_FOEDSELSDATO, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, ETT_TREFF_FEIL_DATO));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.FLERE_TREFF, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, FLERE_TREFF));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.INGEN_TREFF, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, INGEN_TREFF));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.FEIL_STATSBORGERSKAP, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, ETT_TREFF_FEIL_STATSBORGERSKAP));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.PERSON_OPPHORT, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, ETT_TREFF_PERSON_OPPHORT));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.FNR_IKKE_FUNNET, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, FNR_IKKE_FUNNET));
    }

    void counter(final SoekBegrunnelse soekBegrunnelse) {
        var counter = SØKBEGRUNNELSE_TELLERE.get(soekBegrunnelse);
        if (counter != null) {
            counter.increment();
        } else {
            log.warn("Kunne ikke finne teller for søkbegrunnelse {}", soekBegrunnelse);
        }
    }
}
