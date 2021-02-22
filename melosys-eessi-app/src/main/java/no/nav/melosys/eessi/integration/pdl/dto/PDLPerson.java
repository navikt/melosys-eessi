package no.nav.melosys.eessi.integration.pdl.dto;

import java.util.Collection;

import lombok.Data;

@Data
public class PDLPerson {
    private PDLNavn navn;
    private PDLFoedsel foedsel;
    private Collection<PDLStatsborgerskap> statsborgerskap;
    private Collection<PDLFolkeregisterPersonstatus> folkeregisterpersonstatus;
}
