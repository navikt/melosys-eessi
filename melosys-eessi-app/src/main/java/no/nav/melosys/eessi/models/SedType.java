package no.nav.melosys.eessi.models;

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
    H010,
    H011,
    H020,
    H061,
    H065,
    H070,
    H120,
    H121,
    H130,

    S040,
    S041;

    public boolean erXSED() {
        return this.name().startsWith("X");
    }
}
