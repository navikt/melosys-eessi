package no.nav.melosys.eessi.integration.eux.case_store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseStoreDto {
    @JsonProperty("bucId")
    private String bucID;
    @JsonProperty("navId")
    private String fagsaknummer;
    @JsonProperty("rinaId")
    private String rinaSaksnummer;
    @JsonProperty("caseFileId")
    private String journalpostID;
    @JsonProperty("theme")
    private String tema;

    public CaseStoreDto(String fagsaknummer, String rinaSaksnummer) {
        this.fagsaknummer = fagsaknummer;
        this.rinaSaksnummer = rinaSaksnummer;
    }
}
