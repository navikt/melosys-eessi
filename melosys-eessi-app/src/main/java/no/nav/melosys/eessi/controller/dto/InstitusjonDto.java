// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.controller.dto;

public class InstitusjonDto {
    private String id;
    private String navn;
    private String landkode;

    @java.lang.SuppressWarnings("all")
    public String getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public String getNavn() {
        return this.navn;
    }

    @java.lang.SuppressWarnings("all")
    public String getLandkode() {
        return this.landkode;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final String id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setNavn(final String navn) {
        this.navn = navn;
    }

    @java.lang.SuppressWarnings("all")
    public void setLandkode(final String landkode) {
        this.landkode = landkode;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof InstitusjonDto)) return false;
        final InstitusjonDto other = (InstitusjonDto) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$navn = this.getNavn();
        final java.lang.Object other$navn = other.getNavn();
        if (this$navn == null ? other$navn != null : !this$navn.equals(other$navn)) return false;
        final java.lang.Object this$landkode = this.getLandkode();
        final java.lang.Object other$landkode = other.getLandkode();
        if (this$landkode == null ? other$landkode != null : !this$landkode.equals(other$landkode)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof InstitusjonDto;
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
        final java.lang.Object $landkode = this.getLandkode();
        result = result * PRIME + ($landkode == null ? 43 : $landkode.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "InstitusjonDto(id=" + this.getId() + ", navn=" + this.getNavn() + ", landkode=" + this.getLandkode() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public InstitusjonDto(final String id, final String navn, final String landkode) {
        this.id = id;
        this.navn = navn;
        this.landkode = landkode;
    }

    @java.lang.SuppressWarnings("all")
    public InstitusjonDto() {
    }
}
