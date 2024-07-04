// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class SpesielleOmstendigheter {
    private String type;
    private String beskrivelseannensituasjon;

    @java.lang.SuppressWarnings("all")
    public SpesielleOmstendigheter() {
    }

    @java.lang.SuppressWarnings("all")
    public String getType() {
        return this.type;
    }

    @java.lang.SuppressWarnings("all")
    public String getBeskrivelseannensituasjon() {
        return this.beskrivelseannensituasjon;
    }

    @java.lang.SuppressWarnings("all")
    public void setType(final String type) {
        this.type = type;
    }

    @java.lang.SuppressWarnings("all")
    public void setBeskrivelseannensituasjon(final String beskrivelseannensituasjon) {
        this.beskrivelseannensituasjon = beskrivelseannensituasjon;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof SpesielleOmstendigheter)) return false;
        final SpesielleOmstendigheter other = (SpesielleOmstendigheter) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$type = this.getType();
        final java.lang.Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final java.lang.Object this$beskrivelseannensituasjon = this.getBeskrivelseannensituasjon();
        final java.lang.Object other$beskrivelseannensituasjon = other.getBeskrivelseannensituasjon();
        if (this$beskrivelseannensituasjon == null ? other$beskrivelseannensituasjon != null : !this$beskrivelseannensituasjon.equals(other$beskrivelseannensituasjon))
            return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof SpesielleOmstendigheter;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final java.lang.Object $beskrivelseannensituasjon = this.getBeskrivelseannensituasjon();
        result = result * PRIME + ($beskrivelseannensituasjon == null ? 43 : $beskrivelseannensituasjon.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "SpesielleOmstendigheter(type=" + this.getType() + ", beskrivelseannensituasjon=" + this.getBeskrivelseannensituasjon() + ")";
    }
}
