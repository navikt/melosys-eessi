package no.nav.melosys.eessi.controller.dto;

import lombok.Data;

@Data
public class Adresse {

    private String poststed;
    private String postnr;
    private String land;
    private String gateadresse;
    private String region;
    private Adressetype adressetype;
}
