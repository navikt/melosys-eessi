// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

public class IdentRekvisisjonSivilstand {
    private String sivilstand;
    private String sivilstandsdato;
    private String bekreftelsesdato;
    private String relatertVedSivilstand;

    @java.lang.SuppressWarnings("all")
    public String getSivilstand() {
        return this.sivilstand;
    }

    @java.lang.SuppressWarnings("all")
    public String getSivilstandsdato() {
        return this.sivilstandsdato;
    }

    @java.lang.SuppressWarnings("all")
    public String getBekreftelsesdato() {
        return this.bekreftelsesdato;
    }

    @java.lang.SuppressWarnings("all")
    public String getRelatertVedSivilstand() {
        return this.relatertVedSivilstand;
    }

    @java.lang.SuppressWarnings("all")
    public void setSivilstand(final String sivilstand) {
        this.sivilstand = sivilstand;
    }

    @java.lang.SuppressWarnings("all")
    public void setSivilstandsdato(final String sivilstandsdato) {
        this.sivilstandsdato = sivilstandsdato;
    }

    @java.lang.SuppressWarnings("all")
    public void setBekreftelsesdato(final String bekreftelsesdato) {
        this.bekreftelsesdato = bekreftelsesdato;
    }

    @java.lang.SuppressWarnings("all")
    public void setRelatertVedSivilstand(final String relatertVedSivilstand) {
        this.relatertVedSivilstand = relatertVedSivilstand;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof IdentRekvisisjonSivilstand)) return false;
        final IdentRekvisisjonSivilstand other = (IdentRekvisisjonSivilstand) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$sivilstand = this.getSivilstand();
        final java.lang.Object other$sivilstand = other.getSivilstand();
        if (this$sivilstand == null ? other$sivilstand != null : !this$sivilstand.equals(other$sivilstand)) return false;
        final java.lang.Object this$sivilstandsdato = this.getSivilstandsdato();
        final java.lang.Object other$sivilstandsdato = other.getSivilstandsdato();
        if (this$sivilstandsdato == null ? other$sivilstandsdato != null : !this$sivilstandsdato.equals(other$sivilstandsdato)) return false;
        final java.lang.Object this$bekreftelsesdato = this.getBekreftelsesdato();
        final java.lang.Object other$bekreftelsesdato = other.getBekreftelsesdato();
        if (this$bekreftelsesdato == null ? other$bekreftelsesdato != null : !this$bekreftelsesdato.equals(other$bekreftelsesdato)) return false;
        final java.lang.Object this$relatertVedSivilstand = this.getRelatertVedSivilstand();
        final java.lang.Object other$relatertVedSivilstand = other.getRelatertVedSivilstand();
        if (this$relatertVedSivilstand == null ? other$relatertVedSivilstand != null : !this$relatertVedSivilstand.equals(other$relatertVedSivilstand)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof IdentRekvisisjonSivilstand;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $sivilstand = this.getSivilstand();
        result = result * PRIME + ($sivilstand == null ? 43 : $sivilstand.hashCode());
        final java.lang.Object $sivilstandsdato = this.getSivilstandsdato();
        result = result * PRIME + ($sivilstandsdato == null ? 43 : $sivilstandsdato.hashCode());
        final java.lang.Object $bekreftelsesdato = this.getBekreftelsesdato();
        result = result * PRIME + ($bekreftelsesdato == null ? 43 : $bekreftelsesdato.hashCode());
        final java.lang.Object $relatertVedSivilstand = this.getRelatertVedSivilstand();
        result = result * PRIME + ($relatertVedSivilstand == null ? 43 : $relatertVedSivilstand.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "IdentRekvisisjonSivilstand(sivilstand=" + this.getSivilstand() + ", sivilstandsdato=" + this.getSivilstandsdato() + ", bekreftelsesdato=" + this.getBekreftelsesdato() + ", relatertVedSivilstand=" + this.getRelatertVedSivilstand() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public IdentRekvisisjonSivilstand() {
    }

    @java.lang.SuppressWarnings("all")
    public IdentRekvisisjonSivilstand(final String sivilstand, final String sivilstandsdato, final String bekreftelsesdato, final String relatertVedSivilstand) {
        this.sivilstand = sivilstand;
        this.sivilstandsdato = sivilstandsdato;
        this.bekreftelsesdato = bekreftelsesdato;
        this.relatertVedSivilstand = relatertVedSivilstand;
    }
}
