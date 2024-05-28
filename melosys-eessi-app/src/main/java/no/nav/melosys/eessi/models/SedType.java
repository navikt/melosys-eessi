package no.nav.melosys.eessi.models;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public enum SedType {
    X001,
    X002,
    X003,
    X004,
    X005,
    X006,
    X007,
    X008,
    X009,
    X010,
    X011,
    X012,
    X013,
    X050,
    X100,

    A001,
    A002,
    A003,
    A004,
    A005,
    A006,
    A007,
    A008,
    A009,
    A010,
    A011,
    A012,

    H001,
    H002,
    H003,
    H004,
    H005,
    H006,
    H010,
    H011,
    H012,
    H020,
    H021,
    H061,
    H065,
    H066,
    H070,
    H120,
    H121,
    H130,

    S040,
    S041;

    private static final Collection<SedType> LOVVALG_SED_TYPER = Arrays.stream(SedType.values())
        .filter(s -> s.name().startsWith("A"))
        .collect(Collectors.toSet());

    public boolean erXSED() {
        return this.name().startsWith("X");
    }

    public boolean erASED() {
        return this.name().startsWith("A");
    }

    public static boolean erLovvalgSed(String sedType) {
        return LOVVALG_SED_TYPER.stream().anyMatch(s -> s.name().equals(sedType));
    }

    public static final List<SedType> KREVER_ADRESSE = Arrays.asList(SedType.A001, SedType.A002, SedType.A003, SedType.A004, SedType.A007, SedType.A009, SedType.A010);

    public boolean kreverAdresse(){
        return KREVER_ADRESSE.stream().anyMatch(s -> s.equals(this));
    }
}
