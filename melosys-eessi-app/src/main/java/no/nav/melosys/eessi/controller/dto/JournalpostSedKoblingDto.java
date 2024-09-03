// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.controller.dto;

import no.nav.melosys.eessi.models.JournalpostSedKobling;

public class JournalpostSedKoblingDto {
    private String journalpostID;
    private String sedID;
    private String rinaSaksnummer;
    private String bucType;
    private String sedType;

    public JournalpostSedKoblingDto(JournalpostSedKobling fra) {
        this(fra.getJournalpostID(), fra.getSedId(), fra.getRinaSaksnummer(), fra.getBucType(), fra.getSedType());
    }

    @java.lang.SuppressWarnings("all")
    public String getJournalpostID() {
        return this.journalpostID;
    }

    @java.lang.SuppressWarnings("all")
    public String getSedID() {
        return this.sedID;
    }

    @java.lang.SuppressWarnings("all")
    public String getRinaSaksnummer() {
        return this.rinaSaksnummer;
    }

    @java.lang.SuppressWarnings("all")
    public String getBucType() {
        return this.bucType;
    }

    @java.lang.SuppressWarnings("all")
    public String getSedType() {
        return this.sedType;
    }

    @java.lang.SuppressWarnings("all")
    public JournalpostSedKoblingDto() {
    }

    @java.lang.SuppressWarnings("all")
    public JournalpostSedKoblingDto(final String journalpostID, final String sedID, final String rinaSaksnummer, final String bucType, final String sedType) {
        this.journalpostID = journalpostID;
        this.sedID = sedID;
        this.rinaSaksnummer = rinaSaksnummer;
        this.bucType = bucType;
        this.sedType = sedType;
    }
}
