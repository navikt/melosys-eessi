package no.nav.melosys.eessi.metrikker;

public final class MetrikkerNavn {

    public static final String METRIKKER_NAMESPACE = "melosys-eessi.";

    static final String SED_MOTTATT_FEILET_DEPRICATED = METRIKKER_NAMESPACE + "sed.feilet.antall";
    static final String SED_MOTTATT = METRIKKER_NAMESPACE + "sed.mottatt";
    static final String SED_MOTTATT_FEILET = SED_MOTTATT + ".feilet";
    static final String SED_SENDT = METRIKKER_NAMESPACE + "sed.sendt";
    static final String BUC_OPPRETTET = METRIKKER_NAMESPACE + "buc.opprettet";
    static final String BUC_LUKKET = METRIKKER_NAMESPACE + "buc.lukket";

    static final String KEY_SEDTYPE = "sedType";
    static final String KEY_BUCTYPE = "bucType";

    static final String KEY_FEILTYPE = "feilType";

    private MetrikkerNavn() {
    }
}
