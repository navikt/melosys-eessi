package no.nav.melosys.eessi.integration.pdl.dto;

import java.util.Collection;
import java.util.HashSet;

import lombok.Data;

@Data
public class PDLSokPerson {
    private int pageNumber;
    private int totalPages;
    private int totalHits;
    private Collection<PDLSokHit> hits = new HashSet<>();
}
