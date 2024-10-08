// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.pdl.dto;

import static no.nav.melosys.eessi.integration.pdl.dto.PDLIdentGruppe.AKTORID;
import static no.nav.melosys.eessi.integration.pdl.dto.PDLIdentGruppe.FOLKEREGISTERIDENT;

public class PDLIdent {
    private PDLIdentGruppe gruppe;
    private String ident;

    public boolean erFolkeregisterIdent() {
        return gruppe == FOLKEREGISTERIDENT;
    }

    public boolean erAktørID() {
        return gruppe == AKTORID;
    }

    @java.lang.SuppressWarnings("all")
    public PDLIdentGruppe getGruppe() {
        return this.gruppe;
    }

    @java.lang.SuppressWarnings("all")
    public String getIdent() {
        return this.ident;
    }

    @java.lang.SuppressWarnings("all")
    public void setGruppe(final PDLIdentGruppe gruppe) {
        this.gruppe = gruppe;
    }

    @java.lang.SuppressWarnings("all")
    public void setIdent(final String ident) {
        this.ident = ident;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof PDLIdent)) return false;
        final PDLIdent other = (PDLIdent) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$gruppe = this.getGruppe();
        final java.lang.Object other$gruppe = other.getGruppe();
        if (this$gruppe == null ? other$gruppe != null : !this$gruppe.equals(other$gruppe)) return false;
        final java.lang.Object this$ident = this.getIdent();
        final java.lang.Object other$ident = other.getIdent();
        if (this$ident == null ? other$ident != null : !this$ident.equals(other$ident)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof PDLIdent;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $gruppe = this.getGruppe();
        result = result * PRIME + ($gruppe == null ? 43 : $gruppe.hashCode());
        final java.lang.Object $ident = this.getIdent();
        result = result * PRIME + ($ident == null ? 43 : $ident.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "PDLIdent(gruppe=" + this.getGruppe() + ", ident=" + this.getIdent() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public PDLIdent() {
    }

    @java.lang.SuppressWarnings("all")
    public PDLIdent(final PDLIdentGruppe gruppe, final String ident) {
        this.gruppe = gruppe;
        this.ident = ident;
    }
}
