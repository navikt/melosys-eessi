package no.nav.melosys.eessi.integration.pdl.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import lombok.Data;

@Data
public class PDLMetadata {
    private String opplysningsId;
    private String master;
    private Collection<PDLEndring> endringer = Collections.emptyList();
    private boolean historisk;

    public LocalDateTime sisteDatoOpprettetEllerKorrigert() {
        return endringer.stream()
                .filter(PDLEndring::erOpprettelseEllerKorrigering)
                .map(PDLEndring::getRegistrert)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MIN);
    }
}
