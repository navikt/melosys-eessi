// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.service.sed;

public class SedMottattMigreringRapportDto {
    private final String rinaSaksnummer;
    private final String dokumentId;
    private final String journalpostId;

    @java.lang.SuppressWarnings("all")
    public String getRinaSaksnummer() {
        return this.rinaSaksnummer;
    }

    @java.lang.SuppressWarnings("all")
    public String getDokumentId() {
        return this.dokumentId;
    }

    @java.lang.SuppressWarnings("all")
    public String getJournalpostId() {
        return this.journalpostId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof SedMottattMigreringRapportDto)) return false;
        final SedMottattMigreringRapportDto other = (SedMottattMigreringRapportDto) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$rinaSaksnummer = this.getRinaSaksnummer();
        final java.lang.Object other$rinaSaksnummer = other.getRinaSaksnummer();
        if (this$rinaSaksnummer == null ? other$rinaSaksnummer != null : !this$rinaSaksnummer.equals(other$rinaSaksnummer)) return false;
        final java.lang.Object this$dokumentId = this.getDokumentId();
        final java.lang.Object other$dokumentId = other.getDokumentId();
        if (this$dokumentId == null ? other$dokumentId != null : !this$dokumentId.equals(other$dokumentId)) return false;
        final java.lang.Object this$journalpostId = this.getJournalpostId();
        final java.lang.Object other$journalpostId = other.getJournalpostId();
        if (this$journalpostId == null ? other$journalpostId != null : !this$journalpostId.equals(other$journalpostId)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof SedMottattMigreringRapportDto;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $rinaSaksnummer = this.getRinaSaksnummer();
        result = result * PRIME + ($rinaSaksnummer == null ? 43 : $rinaSaksnummer.hashCode());
        final java.lang.Object $dokumentId = this.getDokumentId();
        result = result * PRIME + ($dokumentId == null ? 43 : $dokumentId.hashCode());
        final java.lang.Object $journalpostId = this.getJournalpostId();
        result = result * PRIME + ($journalpostId == null ? 43 : $journalpostId.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "SedMottattMigreringRapportDto(rinaSaksnummer=" + this.getRinaSaksnummer() + ", dokumentId=" + this.getDokumentId() + ", journalpostId=" + this.getJournalpostId() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public SedMottattMigreringRapportDto(final String rinaSaksnummer, final String dokumentId, final String journalpostId) {
        this.rinaSaksnummer = rinaSaksnummer;
        this.dokumentId = dokumentId;
        this.journalpostId = journalpostId;
    }
}
