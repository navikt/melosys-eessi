package no.nav.melosys.eessi.controller.dto;

import lombok.Data;

@Data
public class FamilieMedlem {

    private String relasjon; //FAR ELLER MOR
    private String fornavn;
    private String etternavn;

}
