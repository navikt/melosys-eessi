package no.nav.melosys.eessi.metrikker;

final class MetrikkerNavn {

    private MetrikkerNavn() {
    }

    private static final String METRIKKER_NAMESPACE = "melosys-eessi.";

    static final String SED_MOTTATT = METRIKKER_NAMESPACE + "sed.mottatt";
    static final String SED_SENDT = METRIKKER_NAMESPACE + "sed.sendt";
    static final String IDENTIFISERING_FUNNET = METRIKKER_NAMESPACE + "identifisering.funnet";
    static final String BUC_OPPRETTET = METRIKKER_NAMESPACE + "buc.opprettet";
    static final String BUC_LUKKET = METRIKKER_NAMESPACE + "buc.lukket";
    static final String JOURNALPOST_INNGAAENDE_OPPRETTET = METRIKKER_NAMESPACE + "journalpost.inngaaende.opprettet";
    static final String JOURNALPOST_UTGAAENDE_OPPRETTET = METRIKKER_NAMESPACE + "journalpost.utgaaende.opprettet";
    static final String JOURNALPOST_UTGAAENDE_FERDIGSTILT = METRIKKER_NAMESPACE + "journalpost.utgaaende.ferdigstilt";

    static final String KEY_SEDTYPE = "sedType";
    static final String KEY_BUCTYPE = "bucType";
    static final String KEY_AVSENDERLAND = "avsenderLand";
    static final String KEY_MOTTAKERLAND = "mottakerLand";
    static final String KEY_SED_VERSJON = "sedVersjon";
    static final String KEY_RINASAKSNUMMER = "rinaSaksnummer";
    static final String KEY_PERSON_IDENTIFISERT = "personIdentifisert";


}
