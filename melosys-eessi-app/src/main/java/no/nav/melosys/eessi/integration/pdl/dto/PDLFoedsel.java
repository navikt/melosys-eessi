package no.nav.melosys.eessi.integration.pdl.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PDLFoedsel implements HarMetadata {
    private LocalDate foedselsdato;
    private PDLMetadata metadata;
}
