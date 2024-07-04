// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.pdl.dto;

public class PDLNavn implements HarMetadata {
    private String fornavn;
    private String etternavn;
    private PDLMetadata metadata;

    @java.lang.SuppressWarnings("all")
    public PDLNavn() {
    }

    @java.lang.SuppressWarnings("all")
    public String getFornavn() {
        return this.fornavn;
    }

    @java.lang.SuppressWarnings("all")
    public String getEtternavn() {
        return this.etternavn;
    }

    @java.lang.SuppressWarnings("all")
    public PDLMetadata getMetadata() {
        return this.metadata;
    }

    @java.lang.SuppressWarnings("all")
    public void setFornavn(final String fornavn) {
        this.fornavn = fornavn;
    }

    @java.lang.SuppressWarnings("all")
    public void setEtternavn(final String etternavn) {
        this.etternavn = etternavn;
    }

    @java.lang.SuppressWarnings("all")
    public void setMetadata(final PDLMetadata metadata) {
        this.metadata = metadata;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof PDLNavn)) return false;
        final PDLNavn other = (PDLNavn) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$fornavn = this.getFornavn();
        final java.lang.Object other$fornavn = other.getFornavn();
        if (this$fornavn == null ? other$fornavn != null : !this$fornavn.equals(other$fornavn)) return false;
        final java.lang.Object this$etternavn = this.getEtternavn();
        final java.lang.Object other$etternavn = other.getEtternavn();
        if (this$etternavn == null ? other$etternavn != null : !this$etternavn.equals(other$etternavn)) return false;
        final java.lang.Object this$metadata = this.getMetadata();
        final java.lang.Object other$metadata = other.getMetadata();
        if (this$metadata == null ? other$metadata != null : !this$metadata.equals(other$metadata)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof PDLNavn;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $fornavn = this.getFornavn();
        result = result * PRIME + ($fornavn == null ? 43 : $fornavn.hashCode());
        final java.lang.Object $etternavn = this.getEtternavn();
        result = result * PRIME + ($etternavn == null ? 43 : $etternavn.hashCode());
        final java.lang.Object $metadata = this.getMetadata();
        result = result * PRIME + ($metadata == null ? 43 : $metadata.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "PDLNavn(fornavn=" + this.getFornavn() + ", etternavn=" + this.getEtternavn() + ", metadata=" + this.getMetadata() + ")";
    }
}
