package no.nav.melosys.eessi.integration.pdl.dto;

import java.util.Collection;

import lombok.Data;

@Data
public class PDLSokHit {
    private Double score;
    private Collection<PDLIdent> identer;
}
