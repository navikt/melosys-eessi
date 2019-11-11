package no.nav.melosys.eessi.integration.dokkat.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DokumentTypeInfoDto {
    private String dokumenttypeId;
    private String dokumentTittel;
    private String dokumentType;
    private String dokumentKategori;
    private Boolean sensitivt;
    private String tema;
    private String behandlingstema;
    private String arkivSystem;
    private String artifaktId;
}
