package no.nav.melosys.eessi.integration.eux.case_store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CaseStoreDto {
    @JsonProperty("bucId")
    private String bucID;
    @JsonProperty("navId")
    private String fagsaknummer;
    @JsonProperty("rinaId")
    private String rinaSaksnummer;
}
