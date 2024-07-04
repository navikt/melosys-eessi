// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Institusjon {
    private String id;
    private String navn;

    @java.lang.SuppressWarnings("all")
    public Institusjon() {
    }

    @java.lang.SuppressWarnings("all")
    public String getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public String getNavn() {
        return this.navn;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final String id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setNavn(final String navn) {
        this.navn = navn;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Institusjon)) return false;
        final Institusjon other = (Institusjon) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$navn = this.getNavn();
        final java.lang.Object other$navn = other.getNavn();
        if (this$navn == null ? other$navn != null : !this$navn.equals(other$navn)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Institusjon;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $navn = this.getNavn();
        result = result * PRIME + ($navn == null ? 43 : $navn.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "Institusjon(id=" + this.getId() + ", navn=" + this.getNavn() + ")";
    }
}
