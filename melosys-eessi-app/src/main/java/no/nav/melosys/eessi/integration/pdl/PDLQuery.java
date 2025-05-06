package no.nav.melosys.eessi.integration.pdl;

final class PDLQuery {

    private PDLQuery() {
    }

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
            foedselsdato {
                foedselsdato
                foedselsaar
                metadata {
                    master
                    endringer {
                        registrert
                        type
                        kilde
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
             utenlandskIdentifikasjonsnummer {
                identifikasjonsnummer
                utstederland
             }
             kjoenn {
                kjoenn
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

    static final String SØK_PERSON_QUERY = """
        query ($paging:Paging, $criteria:[Criterion]) {
            sokPerson (paging: $paging,  criteria: $criteria){
                pageNumber,
                totalPages,
                totalHits,
                hits {
                    score,
                    identer {
                        ident
                        gruppe
                    }
                }
            }
        }
        """;

    static final String HENT_IDENTER_QUERY = """
        query($ident: ID!) {
          hentIdenter(ident: $ident) {
              identer {
                  ident,
                  gruppe
              }
          }
        }
        """;

}
