package no.nav.melosys.eessi.integration.pdl.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PDLEndring {
    private String type;
    private LocalDateTime registrert;

    private static final String ENDRINGSTYPE_OPPRETT = "OPPRETT";
    private static final String ENDRINGSTYPE_KORRIGER = "KORRIGER";

    public boolean erOpprettelseEllerKorrigering() {
        return ENDRINGSTYPE_OPPRETT.equals(type) || ENDRINGSTYPE_KORRIGER.equals(type);
    }
}
