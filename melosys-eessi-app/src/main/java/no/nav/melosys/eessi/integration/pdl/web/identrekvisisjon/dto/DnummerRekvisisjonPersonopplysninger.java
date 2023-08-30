package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.melosys.eessi.integration.pdl.dto.PDLKjoennType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonPersonopplysninger {

    private String fornavn;
    private String etternavn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate foedselsdato;
    private PDLKjoennType kjoenn;
    private String foedested;
    private String foedeland;
    private List<String> statsborgerskap;
}
