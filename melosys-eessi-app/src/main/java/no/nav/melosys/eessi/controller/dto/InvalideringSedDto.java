// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.controller.dto;

public class InvalideringSedDto {
    private String sedTypeSomSkalInvalideres;
    private String utstedelsedato;

    @java.lang.SuppressWarnings("all")
    public InvalideringSedDto() {
    }

    @java.lang.SuppressWarnings("all")
    public String getSedTypeSomSkalInvalideres() {
        return this.sedTypeSomSkalInvalideres;
    }

    @java.lang.SuppressWarnings("all")
    public String getUtstedelsedato() {
        return this.utstedelsedato;
    }

    @java.lang.SuppressWarnings("all")
    public void setSedTypeSomSkalInvalideres(final String sedTypeSomSkalInvalideres) {
        this.sedTypeSomSkalInvalideres = sedTypeSomSkalInvalideres;
    }

    @java.lang.SuppressWarnings("all")
    public void setUtstedelsedato(final String utstedelsedato) {
        this.utstedelsedato = utstedelsedato;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof InvalideringSedDto)) return false;
        final InvalideringSedDto other = (InvalideringSedDto) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$sedTypeSomSkalInvalideres = this.getSedTypeSomSkalInvalideres();
        final java.lang.Object other$sedTypeSomSkalInvalideres = other.getSedTypeSomSkalInvalideres();
        if (this$sedTypeSomSkalInvalideres == null ? other$sedTypeSomSkalInvalideres != null : !this$sedTypeSomSkalInvalideres.equals(other$sedTypeSomSkalInvalideres))
            return false;
        final java.lang.Object this$utstedelsedato = this.getUtstedelsedato();
        final java.lang.Object other$utstedelsedato = other.getUtstedelsedato();
        if (this$utstedelsedato == null ? other$utstedelsedato != null : !this$utstedelsedato.equals(other$utstedelsedato))
            return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof InvalideringSedDto;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $sedTypeSomSkalInvalideres = this.getSedTypeSomSkalInvalideres();
        result = result * PRIME + ($sedTypeSomSkalInvalideres == null ? 43 : $sedTypeSomSkalInvalideres.hashCode());
        final java.lang.Object $utstedelsedato = this.getUtstedelsedato();
        result = result * PRIME + ($utstedelsedato == null ? 43 : $utstedelsedato.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "InvalideringSedDto(sedTypeSomSkalInvalideres=" + this.getSedTypeSomSkalInvalideres() + ", utstedelsedato=" + this.getUtstedelsedato() + ")";
    }
}
