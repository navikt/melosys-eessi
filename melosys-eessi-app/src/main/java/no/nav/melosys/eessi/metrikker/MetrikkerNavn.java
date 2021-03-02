package no.nav.melosys.eessi.metrikker;

final class MetrikkerNavn {

    private static final String METRIKKER_NAMESPACE = "melosys-eessi.";

    static final String SED_MOTTATT_FEILET = METRIKKER_NAMESPACE + "sed.feilet.antall";
    static final String SED_MOTTATT = METRIKKER_NAMESPACE + "sed.mottatt";
    static final String SED_SENDT = METRIKKER_NAMESPACE + "sed.sendt";
    static final String IDENTIFISERING = METRIKKER_NAMESPACE + "identifisering";
    static final String BUC_OPPRETTET = METRIKKER_NAMESPACE + "buc.opprettet";
    static final String BUC_LUKKET = METRIKKER_NAMESPACE + "buc.lukket";
    static final String PERSONSØK_SAMMENLIGNING = METRIKKER_NAMESPACE + "personsok.sammenligning";
    static final String PERSONSOK_ANTALL_TPS = METRIKKER_NAMESPACE + "personsok.antall.tps";
    static final String PERSONSOK_ANTALL_PDL = METRIKKER_NAMESPACE + "personsok.antall.pdl";

    static final String KEY_SEDTYPE = "sedType";
    static final String KEY_BUCTYPE = "bucType";
    static final String KEY_RESULTAT = "resultat";

    private MetrikkerNavn() {
    }
}
