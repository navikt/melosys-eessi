package no.nav.melosys.eessi.integration.pdl;

final class PDLQuery {

    private PDLQuery() {}

    static final String HENT_PERSON_QUERY = """
            query($ident: ID!) {
                hentPerson(ident: $ident) {
                    navn {
                        fornavn
                        etternavn
                    }
                    foedsel {
                        foedselsdato
                    }
                    statsborgerskap {
                        land
                    }
                    folkeregisterpersonstatus {
                        status
                    }
                }
            }
            """;
}
