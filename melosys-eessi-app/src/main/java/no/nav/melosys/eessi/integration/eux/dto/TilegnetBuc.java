package no.nav.melosys.eessi.integration.eux.dto;

import java.time.ZonedDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class TilegnetBuc {

    private String bucType;
    private Boolean eessiklar;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime gyldigStartDato;
    private String institusjonsrolle;

    public boolean erEessiKlar() {
        return eessiklar && ZonedDateTime.now().isAfter(gyldigStartDato);
    }
}
