package no.nav.melosys.eessi.service.eux;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BucSearch {

    private String fnr;
    private String fornavn;
    private String etternavn;
    private String foedselsdato;
    private String rinaSaksnummer;
    private String bucType;
    private String status;
}
