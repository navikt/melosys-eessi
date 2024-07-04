// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.pdl.dto;

import java.time.LocalDate;

public class PDLFoedsel implements HarMetadata {
    private LocalDate foedselsdato;
    private PDLMetadata metadata;

    @java.lang.SuppressWarnings("all")
    public PDLFoedsel() {
    }

    @java.lang.SuppressWarnings("all")
    public LocalDate getFoedselsdato() {
        return this.foedselsdato;
    }

    @java.lang.SuppressWarnings("all")
    public PDLMetadata getMetadata() {
        return this.metadata;
    }

    @java.lang.SuppressWarnings("all")
    public void setFoedselsdato(final LocalDate foedselsdato) {
        this.foedselsdato = foedselsdato;
    }

    @java.lang.SuppressWarnings("all")
    public void setMetadata(final PDLMetadata metadata) {
        this.metadata = metadata;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof PDLFoedsel)) return false;
        final PDLFoedsel other = (PDLFoedsel) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$foedselsdato = this.getFoedselsdato();
        final java.lang.Object other$foedselsdato = other.getFoedselsdato();
        if (this$foedselsdato == null ? other$foedselsdato != null : !this$foedselsdato.equals(other$foedselsdato))
            return false;
        final java.lang.Object this$metadata = this.getMetadata();
        final java.lang.Object other$metadata = other.getMetadata();
        if (this$metadata == null ? other$metadata != null : !this$metadata.equals(other$metadata)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof PDLFoedsel;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $foedselsdato = this.getFoedselsdato();
        result = result * PRIME + ($foedselsdato == null ? 43 : $foedselsdato.hashCode());
        final java.lang.Object $metadata = this.getMetadata();
        result = result * PRIME + ($metadata == null ? 43 : $metadata.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "PDLFoedsel(foedselsdato=" + this.getFoedselsdato() + ", metadata=" + this.getMetadata() + ")";
    }
}
