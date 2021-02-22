package no.nav.melosys.eessi.integration.pdl;

final class PDLQuery {

    private PDLQuery() {}

    static final String HENT_PERSON_QUERY = "query($ident: ID!) {\n" +
            "    hentPerson(ident: $ident) {\n" +
            "        navn {\n" +
            "            fornavn\n" +
            "            etternavn\n" +
            "        }\n" +
            "        foedsel {\n" +
            "            foedselsdato\n" +
            "        }\n" +
            "        statsborgerskap {\n" +
            "            land\n" +
            "        }\n" +
            "        folkeregisterpersonstatus {\n" +
            "            status\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
}
