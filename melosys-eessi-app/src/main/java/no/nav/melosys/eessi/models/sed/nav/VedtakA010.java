// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VedtakA010 extends Vedtak {
    private String gjeldervarighetyrkesaktivitet;
    private PeriodeA010 gjelderperiode;
    private String land;

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof VedtakA010)) return false;
        final VedtakA010 other = (VedtakA010) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (!super.equals(o)) return false;
        final java.lang.Object this$gjeldervarighetyrkesaktivitet = this.getGjeldervarighetyrkesaktivitet();
        final java.lang.Object other$gjeldervarighetyrkesaktivitet = other.getGjeldervarighetyrkesaktivitet();
        if (this$gjeldervarighetyrkesaktivitet == null ? other$gjeldervarighetyrkesaktivitet != null : !this$gjeldervarighetyrkesaktivitet.equals(other$gjeldervarighetyrkesaktivitet))
            return false;
        final java.lang.Object this$gjelderperiode = this.getGjelderperiode();
        final java.lang.Object other$gjelderperiode = other.getGjelderperiode();
        if (this$gjelderperiode == null ? other$gjelderperiode != null : !this$gjelderperiode.equals(other$gjelderperiode))
            return false;
        final java.lang.Object this$land = this.getLand();
        final java.lang.Object other$land = other.getLand();
        if (this$land == null ? other$land != null : !this$land.equals(other$land)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof VedtakA010;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final java.lang.Object $gjeldervarighetyrkesaktivitet = this.getGjeldervarighetyrkesaktivitet();
        result = result * PRIME + ($gjeldervarighetyrkesaktivitet == null ? 43 : $gjeldervarighetyrkesaktivitet.hashCode());
        final java.lang.Object $gjelderperiode = this.getGjelderperiode();
        result = result * PRIME + ($gjelderperiode == null ? 43 : $gjelderperiode.hashCode());
        final java.lang.Object $land = this.getLand();
        result = result * PRIME + ($land == null ? 43 : $land.hashCode());
        return result;
    }

    @java.lang.SuppressWarnings("all")
    public VedtakA010() {
    }

    @java.lang.SuppressWarnings("all")
    public String getGjeldervarighetyrkesaktivitet() {
        return this.gjeldervarighetyrkesaktivitet;
    }

    @java.lang.SuppressWarnings("all")
    public PeriodeA010 getGjelderperiode() {
        return this.gjelderperiode;
    }

    @java.lang.SuppressWarnings("all")
    public String getLand() {
        return this.land;
    }

    @java.lang.SuppressWarnings("all")
    public void setGjeldervarighetyrkesaktivitet(final String gjeldervarighetyrkesaktivitet) {
        this.gjeldervarighetyrkesaktivitet = gjeldervarighetyrkesaktivitet;
    }

    @java.lang.SuppressWarnings("all")
    public void setGjelderperiode(final PeriodeA010 gjelderperiode) {
        this.gjelderperiode = gjelderperiode;
    }

    @java.lang.SuppressWarnings("all")
    public void setLand(final String land) {
        this.land = land;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "VedtakA010(gjeldervarighetyrkesaktivitet=" + this.getGjeldervarighetyrkesaktivitet() + ", gjelderperiode=" + this.getGjelderperiode() + ", land=" + this.getLand() + ")";
    }
}
