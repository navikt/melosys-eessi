// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.kafka.producers.model;

import java.time.LocalDate;

public class Periode {
    private LocalDate fom;
    private LocalDate tom;

    @java.lang.SuppressWarnings("all")
    public LocalDate getFom() {
        return this.fom;
    }

    @java.lang.SuppressWarnings("all")
    public LocalDate getTom() {
        return this.tom;
    }

    @java.lang.SuppressWarnings("all")
    public void setFom(final LocalDate fom) {
        this.fom = fom;
    }

    @java.lang.SuppressWarnings("all")
    public void setTom(final LocalDate tom) {
        this.tom = tom;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Periode)) return false;
        final Periode other = (Periode) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$fom = this.getFom();
        final java.lang.Object other$fom = other.getFom();
        if (this$fom == null ? other$fom != null : !this$fom.equals(other$fom)) return false;
        final java.lang.Object this$tom = this.getTom();
        final java.lang.Object other$tom = other.getTom();
        if (this$tom == null ? other$tom != null : !this$tom.equals(other$tom)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Periode;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $fom = this.getFom();
        result = result * PRIME + ($fom == null ? 43 : $fom.hashCode());
        final java.lang.Object $tom = this.getTom();
        result = result * PRIME + ($tom == null ? 43 : $tom.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "Periode(fom=" + this.getFom() + ", tom=" + this.getTom() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public Periode(final LocalDate fom, final LocalDate tom) {
        this.fom = fom;
        this.tom = tom;
    }

    @java.lang.SuppressWarnings("all")
    public Periode() {
    }
}
