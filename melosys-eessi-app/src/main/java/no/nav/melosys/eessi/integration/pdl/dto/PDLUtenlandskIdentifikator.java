// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.pdl.dto;

public class PDLUtenlandskIdentifikator {
    private String identifikasjonsnummer;
    private String utstederland;

    @java.lang.SuppressWarnings("all")
    public PDLUtenlandskIdentifikator() {
    }

    @java.lang.SuppressWarnings("all")
    public String getIdentifikasjonsnummer() {
        return this.identifikasjonsnummer;
    }

    @java.lang.SuppressWarnings("all")
    public String getUtstederland() {
        return this.utstederland;
    }

    @java.lang.SuppressWarnings("all")
    public void setIdentifikasjonsnummer(final String identifikasjonsnummer) {
        this.identifikasjonsnummer = identifikasjonsnummer;
    }

    @java.lang.SuppressWarnings("all")
    public void setUtstederland(final String utstederland) {
        this.utstederland = utstederland;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof PDLUtenlandskIdentifikator)) return false;
        final PDLUtenlandskIdentifikator other = (PDLUtenlandskIdentifikator) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$identifikasjonsnummer = this.getIdentifikasjonsnummer();
        final java.lang.Object other$identifikasjonsnummer = other.getIdentifikasjonsnummer();
        if (this$identifikasjonsnummer == null ? other$identifikasjonsnummer != null : !this$identifikasjonsnummer.equals(other$identifikasjonsnummer))
            return false;
        final java.lang.Object this$utstederland = this.getUtstederland();
        final java.lang.Object other$utstederland = other.getUtstederland();
        if (this$utstederland == null ? other$utstederland != null : !this$utstederland.equals(other$utstederland))
            return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof PDLUtenlandskIdentifikator;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $identifikasjonsnummer = this.getIdentifikasjonsnummer();
        result = result * PRIME + ($identifikasjonsnummer == null ? 43 : $identifikasjonsnummer.hashCode());
        final java.lang.Object $utstederland = this.getUtstederland();
        result = result * PRIME + ($utstederland == null ? 43 : $utstederland.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "PDLUtenlandskIdentifikator(identifikasjonsnummer=" + this.getIdentifikasjonsnummer() + ", utstederland=" + this.getUtstederland() + ")";
    }
}
