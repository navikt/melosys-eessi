package no.nav.melosys.eessi.metrikker;

public final class MetrikkerNavn {

    public static final String METRIKKER_NAMESPACE = "melosys-eessi.";

    static final String KAFKA_DLQ_ANTALL = METRIKKER_NAMESPACE + "kafka.dlq.antall";
    static final String SED_MOTTATT_FEILET_DEPRECATED = METRIKKER_NAMESPACE + "sed.feilet.antall";
    static final String SED_MOTTATT = METRIKKER_NAMESPACE + "sed.mottatt";
    static final String SED_MOTTATT_FEILET = SED_MOTTATT + ".feilet";

    static final String SED_MOTTATT_ALLEREDEJOURNALFOERT = SED_MOTTATT + ".alleredejournalfoert";
    static final String SED_SENDT = METRIKKER_NAMESPACE + "sed.sendt";
    static final String SED_KONVERTERING_FEILET = METRIKKER_NAMESPACE + "sed.konvertering.feilet.antall";
    static final String BUC_OPPRETTET = METRIKKER_NAMESPACE + "buc.opprettet";
    static final String BUC_LUKKET = METRIKKER_NAMESPACE + "buc.lukket";

    static final String KEY_SEDTYPE = "sedType";
    static final String KEY_BUCTYPE = "bucType";

    private MetrikkerNavn() {
    }
}
