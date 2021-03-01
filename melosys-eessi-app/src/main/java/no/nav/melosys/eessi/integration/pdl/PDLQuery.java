package no.nav.melosys.eessi.integration.pdl;

final class PDLQuery {

    private PDLQuery() {}

    static final String HENT_PERSON_QUERY = """
            query($ident: ID!) {
               hentPerson(ident: $ident) {
                 navn {
                     fornavn
                     etternavn
                     metadata {
                         master
                         endringer {
                             registrert
                             type
                         }
                     }
                 }
                 foedsel {
                     foedselsdato
                     metadata {
                         master
                         endringer {
                             registrert
                             type
                         }
                     }
                 }
                 statsborgerskap {
                     land
                 }
                 folkeregisterpersonstatus {
                     status
                     metadata {
                         master
                         endringer {
                             registrert
                             type
                         }
                     }
                 }
               }
             }
            """;
}
