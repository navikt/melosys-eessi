package no.nav.melosys.eessi.integration.pdl.dto;

import lombok.Data;

@Data
public class PDLNavn implements HarMetadata {
    private String fornavn;
    private String etternavn;
    private PDLMetadata metadata;
}
