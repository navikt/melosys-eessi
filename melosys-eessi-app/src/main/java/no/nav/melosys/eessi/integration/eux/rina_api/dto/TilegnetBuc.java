package no.nav.melosys.eessi.integration.eux.rina_api.dto;

import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class TilegnetBuc {

    private String bucType;
    private Boolean eessiklar;
    private ZonedDateTime gyldigStartDato;
    private String institusjonsrolle;

    public boolean erEessiKlar() {
        return eessiklar && ZonedDateTime.now().isAfter(gyldigStartDato);
    }
}
