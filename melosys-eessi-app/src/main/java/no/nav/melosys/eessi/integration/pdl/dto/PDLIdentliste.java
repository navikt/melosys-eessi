// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.pdl.dto;

import java.util.Collection;

public class PDLIdentliste {
    private Collection<PDLIdent> identer;

    @java.lang.SuppressWarnings("all")
    public PDLIdentliste() {
    }

    @java.lang.SuppressWarnings("all")
    public Collection<PDLIdent> getIdenter() {
        return this.identer;
    }

    @java.lang.SuppressWarnings("all")
    public void setIdenter(final Collection<PDLIdent> identer) {
        this.identer = identer;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof PDLIdentliste)) return false;
        final PDLIdentliste other = (PDLIdentliste) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$identer = this.getIdenter();
        final java.lang.Object other$identer = other.getIdenter();
        if (this$identer == null ? other$identer != null : !this$identer.equals(other$identer)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof PDLIdentliste;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $identer = this.getIdenter();
        result = result * PRIME + ($identer == null ? 43 : $identer.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "PDLIdentliste(identer=" + this.getIdenter() + ")";
    }
}
