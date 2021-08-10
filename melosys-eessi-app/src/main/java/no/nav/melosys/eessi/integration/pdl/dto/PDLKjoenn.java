package no.nav.melosys.eessi.integration.pdl.dto;

import lombok.Data;

@Data
public class PDLKjoenn implements HarMetadata {
    private PDLKjoennType kjoenn;
    private PDLMetadata metadata;
}
