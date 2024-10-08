// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.identifisering;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.metrikker.MetrikkerNavn.METRIKKER_NAMESPACE;

@Component
class PersonSokMetrikker {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PersonSokMetrikker.class);
    private static final String IDENTIFISERING = METRIKKER_NAMESPACE + "identifisering";
    private static final String IDENTIFISERING_KONTROLL = IDENTIFISERING + ".kontroll";
    private static final String KEY_RESULTAT = "resultat";
    private static final String IDENTIFISERT = "identifisert";
    private static final String INGEN_TREFF = "nullTreff";
    private static final String FLERE_TREFF = "flereTreff";
    private static final String ETT_TREFF_FEIL_DATO = "feilFoedselsdato";
    private static final String ETT_TREFF_FEIL_STATSBORGERSKAP = "feilStatsborgerskap";
    private static final String ETT_TREFF_PERSON_OPPHORT = "personOpphort";
    private static final String FNR_IKKE_FUNNET = "fnrIkkeFunnet";
    private static final String KEY_BEGRUNNELSE = "kontroll";
    private static final String FØDSELSNUMMER = "fodselsnummer";
    private static final String KJØNN = "kjonn";
    private static final String STATSBORGERSKAP = "statsborgerskap";
    private static final String UTENLANDSK_ID = "utenlandskid";
    private static final String OVERSTYREKONTROLL = "overstyreKontroll";
    private static final Map<SoekBegrunnelse, Counter> SØKBEGRUNNELSE_TELLERE = new EnumMap<>(SoekBegrunnelse.class);
    private static final Map<IdentifiseringsKontrollBegrunnelse, Counter> IDENTIFISERINGS_KONTROLL_BEGRUNNELSE_TELLERE = new EnumMap<>(IdentifiseringsKontrollBegrunnelse.class);

    static {
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.IDENTIFISERT, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, IDENTIFISERT));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.FEIL_FOEDSELSDATO, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, ETT_TREFF_FEIL_DATO));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.FLERE_TREFF, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, FLERE_TREFF));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.INGEN_TREFF, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, INGEN_TREFF));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.FEIL_STATSBORGERSKAP, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, ETT_TREFF_FEIL_STATSBORGERSKAP));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.PERSON_OPPHORT, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, ETT_TREFF_PERSON_OPPHORT));
        SØKBEGRUNNELSE_TELLERE.put(SoekBegrunnelse.FNR_IKKE_FUNNET, Metrics.counter(IDENTIFISERING, KEY_RESULTAT, FNR_IKKE_FUNNET));
        IDENTIFISERINGS_KONTROLL_BEGRUNNELSE_TELLERE.put(IdentifiseringsKontrollBegrunnelse.FØDSELSDATO, Metrics.counter(IDENTIFISERING_KONTROLL, KEY_BEGRUNNELSE, FØDSELSNUMMER));
        IDENTIFISERINGS_KONTROLL_BEGRUNNELSE_TELLERE.put(IdentifiseringsKontrollBegrunnelse.KJØNN, Metrics.counter(IDENTIFISERING_KONTROLL, KEY_BEGRUNNELSE, KJØNN));
        IDENTIFISERINGS_KONTROLL_BEGRUNNELSE_TELLERE.put(IdentifiseringsKontrollBegrunnelse.STATSBORGERSKAP, Metrics.counter(IDENTIFISERING_KONTROLL, KEY_BEGRUNNELSE, STATSBORGERSKAP));
        IDENTIFISERINGS_KONTROLL_BEGRUNNELSE_TELLERE.put(IdentifiseringsKontrollBegrunnelse.UTENLANDSK_ID, Metrics.counter(IDENTIFISERING_KONTROLL, KEY_BEGRUNNELSE, UTENLANDSK_ID));
        IDENTIFISERINGS_KONTROLL_BEGRUNNELSE_TELLERE.put(IdentifiseringsKontrollBegrunnelse.OVERSTYREKONTROLL, Metrics.counter(IDENTIFISERING_KONTROLL, KEY_BEGRUNNELSE, OVERSTYREKONTROLL));
    }

    void counter(final SoekBegrunnelse soekBegrunnelse) {
        inkrementerTeller(SØKBEGRUNNELSE_TELLERE, soekBegrunnelse);
    }

    void counter(final IdentifiseringsKontrollBegrunnelse identifiseringsKontrollBegrunnelse) {
        inkrementerTeller(IDENTIFISERINGS_KONTROLL_BEGRUNNELSE_TELLERE, identifiseringsKontrollBegrunnelse);
    }

    private static <T> void inkrementerTeller(Map<T, Counter> counterMap, T enumVerdi) {
        Optional.ofNullable(counterMap.get(enumVerdi)).ifPresentOrElse(Counter::increment, () -> log.warn("Kunne ikke finne teller for enum {}, verdi {}", enumVerdi.getClass().getSimpleName(), enumVerdi));
    }
}
