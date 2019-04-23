package no.nav.melosys.eessi.integration.eux.dto;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class TilegnetBuc {

    private String bucType;
    private Boolean eessiklar;
    private String gyldigStartDato;
    private String institusjonsrolle;
}
