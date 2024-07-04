// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class AapenPeriode {
    @JsonProperty("type")
    private String ukjentEllerÅpenSluttdato;
    private String startdato;

    @java.lang.SuppressWarnings("all")
    public AapenPeriode() {
    }

    @java.lang.SuppressWarnings("all")
    public String getUkjentEllerÅpenSluttdato() {
        return this.ukjentEllerÅpenSluttdato;
    }

    @java.lang.SuppressWarnings("all")
    public String getStartdato() {
        return this.startdato;
    }

    @JsonProperty("type")
    @java.lang.SuppressWarnings("all")
    public void setUkjentEllerÅpenSluttdato(final String ukjentEllerÅpenSluttdato) {
        this.ukjentEllerÅpenSluttdato = ukjentEllerÅpenSluttdato;
    }

    @java.lang.SuppressWarnings("all")
    public void setStartdato(final String startdato) {
        this.startdato = startdato;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof AapenPeriode)) return false;
        final AapenPeriode other = (AapenPeriode) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$ukjentEllerÅpenSluttdato = this.getUkjentEllerÅpenSluttdato();
        final java.lang.Object other$ukjentEllerÅpenSluttdato = other.getUkjentEllerÅpenSluttdato();
        if (this$ukjentEllerÅpenSluttdato == null ? other$ukjentEllerÅpenSluttdato != null : !this$ukjentEllerÅpenSluttdato.equals(other$ukjentEllerÅpenSluttdato)) return false;
        final java.lang.Object this$startdato = this.getStartdato();
        final java.lang.Object other$startdato = other.getStartdato();
        if (this$startdato == null ? other$startdato != null : !this$startdato.equals(other$startdato)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof AapenPeriode;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $ukjentEllerÅpenSluttdato = this.getUkjentEllerÅpenSluttdato();
        result = result * PRIME + ($ukjentEllerÅpenSluttdato == null ? 43 : $ukjentEllerÅpenSluttdato.hashCode());
        final java.lang.Object $startdato = this.getStartdato();
        result = result * PRIME + ($startdato == null ? 43 : $startdato.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "AapenPeriode(ukjentEllerÅpenSluttdato=" + this.getUkjentEllerÅpenSluttdato() + ", startdato=" + this.getStartdato() + ")";
    }
}
