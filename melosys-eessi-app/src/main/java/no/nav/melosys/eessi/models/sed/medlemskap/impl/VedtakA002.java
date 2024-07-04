// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.melosys.eessi.models.sed.nav.Periode;

public class VedtakA002 {
    private Periode annenperiode;
    private String begrunnelse;
    @JsonProperty("id")
    private String resultat;

    @java.lang.SuppressWarnings("all")
    public VedtakA002() {
    }

    @java.lang.SuppressWarnings("all")
    public Periode getAnnenperiode() {
        return this.annenperiode;
    }

    @java.lang.SuppressWarnings("all")
    public String getBegrunnelse() {
        return this.begrunnelse;
    }

    @java.lang.SuppressWarnings("all")
    public String getResultat() {
        return this.resultat;
    }

    @java.lang.SuppressWarnings("all")
    public void setAnnenperiode(final Periode annenperiode) {
        this.annenperiode = annenperiode;
    }

    @java.lang.SuppressWarnings("all")
    public void setBegrunnelse(final String begrunnelse) {
        this.begrunnelse = begrunnelse;
    }

    @JsonProperty("id")
    @java.lang.SuppressWarnings("all")
    public void setResultat(final String resultat) {
        this.resultat = resultat;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof VedtakA002)) return false;
        final VedtakA002 other = (VedtakA002) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$annenperiode = this.getAnnenperiode();
        final java.lang.Object other$annenperiode = other.getAnnenperiode();
        if (this$annenperiode == null ? other$annenperiode != null : !this$annenperiode.equals(other$annenperiode)) return false;
        final java.lang.Object this$begrunnelse = this.getBegrunnelse();
        final java.lang.Object other$begrunnelse = other.getBegrunnelse();
        if (this$begrunnelse == null ? other$begrunnelse != null : !this$begrunnelse.equals(other$begrunnelse)) return false;
        final java.lang.Object this$resultat = this.getResultat();
        final java.lang.Object other$resultat = other.getResultat();
        if (this$resultat == null ? other$resultat != null : !this$resultat.equals(other$resultat)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof VedtakA002;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $annenperiode = this.getAnnenperiode();
        result = result * PRIME + ($annenperiode == null ? 43 : $annenperiode.hashCode());
        final java.lang.Object $begrunnelse = this.getBegrunnelse();
        result = result * PRIME + ($begrunnelse == null ? 43 : $begrunnelse.hashCode());
        final java.lang.Object $resultat = this.getResultat();
        result = result * PRIME + ($resultat == null ? 43 : $resultat.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "VedtakA002(annenperiode=" + this.getAnnenperiode() + ", begrunnelse=" + this.getBegrunnelse() + ", resultat=" + this.getResultat() + ")";
    }
}
