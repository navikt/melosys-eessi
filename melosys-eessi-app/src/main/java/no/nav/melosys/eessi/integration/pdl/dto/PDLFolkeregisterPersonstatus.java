package no.nav.melosys.eessi.integration.pdl.dto;

import lombok.Data;

@Data
public class PDLFolkeregisterPersonstatus implements HarMetadata {

    private static final String STATUS_OPPHØRT = "opphoert";

    private String status;
    private PDLMetadata metadata;

    public boolean statusErOpphørt() {
        return STATUS_OPPHØRT.equals(status);
    }
}
