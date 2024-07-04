// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.models;

public class SedKontekst {
    //Person-søk
    private boolean forsoktIdentifisert;
    private String navIdent;
    //Opprette journalpost
    private String journalpostID;
    private String dokumentID;
    private String gsakSaksnummer;
    //Oppgave til ID og fordeling
    private String oppgaveID;
    //Publisert til kafka
    private boolean publisertKafka;

    public boolean journalpostOpprettet() {
        return journalpostID != null && !journalpostID.isEmpty();
    }

    public boolean personErIdentifisert() {
        return navIdent != null && !navIdent.isEmpty();
    }

    public boolean identifiseringsOppgaveOpprettet() {
        return oppgaveID != null && !oppgaveID.isEmpty();
    }

    @java.lang.SuppressWarnings("all")
    public boolean isForsoktIdentifisert() {
        return this.forsoktIdentifisert;
    }

    @java.lang.SuppressWarnings("all")
    public String getNavIdent() {
        return this.navIdent;
    }

    @java.lang.SuppressWarnings("all")
    public String getJournalpostID() {
        return this.journalpostID;
    }

    @java.lang.SuppressWarnings("all")
    public String getDokumentID() {
        return this.dokumentID;
    }

    @java.lang.SuppressWarnings("all")
    public String getGsakSaksnummer() {
        return this.gsakSaksnummer;
    }

    @java.lang.SuppressWarnings("all")
    public String getOppgaveID() {
        return this.oppgaveID;
    }

    @java.lang.SuppressWarnings("all")
    public boolean isPublisertKafka() {
        return this.publisertKafka;
    }

    @java.lang.SuppressWarnings("all")
    public void setForsoktIdentifisert(final boolean forsoktIdentifisert) {
        this.forsoktIdentifisert = forsoktIdentifisert;
    }

    @java.lang.SuppressWarnings("all")
    public void setNavIdent(final String navIdent) {
        this.navIdent = navIdent;
    }

    @java.lang.SuppressWarnings("all")
    public void setJournalpostID(final String journalpostID) {
        this.journalpostID = journalpostID;
    }

    @java.lang.SuppressWarnings("all")
    public void setDokumentID(final String dokumentID) {
        this.dokumentID = dokumentID;
    }

    @java.lang.SuppressWarnings("all")
    public void setGsakSaksnummer(final String gsakSaksnummer) {
        this.gsakSaksnummer = gsakSaksnummer;
    }

    @java.lang.SuppressWarnings("all")
    public void setOppgaveID(final String oppgaveID) {
        this.oppgaveID = oppgaveID;
    }

    @java.lang.SuppressWarnings("all")
    public void setPublisertKafka(final boolean publisertKafka) {
        this.publisertKafka = publisertKafka;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof SedKontekst)) return false;
        final SedKontekst other = (SedKontekst) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (this.isForsoktIdentifisert() != other.isForsoktIdentifisert()) return false;
        if (this.isPublisertKafka() != other.isPublisertKafka()) return false;
        final java.lang.Object this$navIdent = this.getNavIdent();
        final java.lang.Object other$navIdent = other.getNavIdent();
        if (this$navIdent == null ? other$navIdent != null : !this$navIdent.equals(other$navIdent)) return false;
        final java.lang.Object this$journalpostID = this.getJournalpostID();
        final java.lang.Object other$journalpostID = other.getJournalpostID();
        if (this$journalpostID == null ? other$journalpostID != null : !this$journalpostID.equals(other$journalpostID))
            return false;
        final java.lang.Object this$dokumentID = this.getDokumentID();
        final java.lang.Object other$dokumentID = other.getDokumentID();
        if (this$dokumentID == null ? other$dokumentID != null : !this$dokumentID.equals(other$dokumentID))
            return false;
        final java.lang.Object this$gsakSaksnummer = this.getGsakSaksnummer();
        final java.lang.Object other$gsakSaksnummer = other.getGsakSaksnummer();
        if (this$gsakSaksnummer == null ? other$gsakSaksnummer != null : !this$gsakSaksnummer.equals(other$gsakSaksnummer))
            return false;
        final java.lang.Object this$oppgaveID = this.getOppgaveID();
        final java.lang.Object other$oppgaveID = other.getOppgaveID();
        if (this$oppgaveID == null ? other$oppgaveID != null : !this$oppgaveID.equals(other$oppgaveID)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof SedKontekst;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isForsoktIdentifisert() ? 79 : 97);
        result = result * PRIME + (this.isPublisertKafka() ? 79 : 97);
        final java.lang.Object $navIdent = this.getNavIdent();
        result = result * PRIME + ($navIdent == null ? 43 : $navIdent.hashCode());
        final java.lang.Object $journalpostID = this.getJournalpostID();
        result = result * PRIME + ($journalpostID == null ? 43 : $journalpostID.hashCode());
        final java.lang.Object $dokumentID = this.getDokumentID();
        result = result * PRIME + ($dokumentID == null ? 43 : $dokumentID.hashCode());
        final java.lang.Object $gsakSaksnummer = this.getGsakSaksnummer();
        result = result * PRIME + ($gsakSaksnummer == null ? 43 : $gsakSaksnummer.hashCode());
        final java.lang.Object $oppgaveID = this.getOppgaveID();
        result = result * PRIME + ($oppgaveID == null ? 43 : $oppgaveID.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "SedKontekst(forsoktIdentifisert=" + this.isForsoktIdentifisert() + ", navIdent=" + this.getNavIdent() + ", journalpostID=" + this.getJournalpostID() + ", dokumentID=" + this.getDokumentID() + ", gsakSaksnummer=" + this.getGsakSaksnummer() + ", oppgaveID=" + this.getOppgaveID() + ", publisertKafka=" + this.isPublisertKafka() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public SedKontekst() {
    }
}
