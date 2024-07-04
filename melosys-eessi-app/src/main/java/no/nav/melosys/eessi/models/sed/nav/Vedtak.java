// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.models.sed.nav;

public abstract class Vedtak {
    private String datoforrigevedtak;
    private String eropprinneligvedtak; // RINA regler: Kan bare sette "ja" eller null (default: null, som betyr nei)
    private String erendringsvedtak; // RINA regler: Kan bare sette "nei" eller null (default: null, som betyr ja)

    @java.lang.SuppressWarnings("all")
    public Vedtak() {
    }

    @java.lang.SuppressWarnings("all")
    public String getDatoforrigevedtak() {
        return this.datoforrigevedtak;
    }

    @java.lang.SuppressWarnings("all")
    public String getEropprinneligvedtak() {
        return this.eropprinneligvedtak;
    }

    @java.lang.SuppressWarnings("all")
    public String getErendringsvedtak() {
        return this.erendringsvedtak;
    }

    @java.lang.SuppressWarnings("all")
    public void setDatoforrigevedtak(final String datoforrigevedtak) {
        this.datoforrigevedtak = datoforrigevedtak;
    }

    @java.lang.SuppressWarnings("all")
    public void setEropprinneligvedtak(final String eropprinneligvedtak) {
        this.eropprinneligvedtak = eropprinneligvedtak;
    }

    @java.lang.SuppressWarnings("all")
    public void setErendringsvedtak(final String erendringsvedtak) {
        this.erendringsvedtak = erendringsvedtak;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Vedtak)) return false;
        final Vedtak other = (Vedtak) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$datoforrigevedtak = this.getDatoforrigevedtak();
        final java.lang.Object other$datoforrigevedtak = other.getDatoforrigevedtak();
        if (this$datoforrigevedtak == null ? other$datoforrigevedtak != null : !this$datoforrigevedtak.equals(other$datoforrigevedtak))
            return false;
        final java.lang.Object this$eropprinneligvedtak = this.getEropprinneligvedtak();
        final java.lang.Object other$eropprinneligvedtak = other.getEropprinneligvedtak();
        if (this$eropprinneligvedtak == null ? other$eropprinneligvedtak != null : !this$eropprinneligvedtak.equals(other$eropprinneligvedtak))
            return false;
        final java.lang.Object this$erendringsvedtak = this.getErendringsvedtak();
        final java.lang.Object other$erendringsvedtak = other.getErendringsvedtak();
        if (this$erendringsvedtak == null ? other$erendringsvedtak != null : !this$erendringsvedtak.equals(other$erendringsvedtak))
            return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Vedtak;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $datoforrigevedtak = this.getDatoforrigevedtak();
        result = result * PRIME + ($datoforrigevedtak == null ? 43 : $datoforrigevedtak.hashCode());
        final java.lang.Object $eropprinneligvedtak = this.getEropprinneligvedtak();
        result = result * PRIME + ($eropprinneligvedtak == null ? 43 : $eropprinneligvedtak.hashCode());
        final java.lang.Object $erendringsvedtak = this.getErendringsvedtak();
        result = result * PRIME + ($erendringsvedtak == null ? 43 : $erendringsvedtak.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "Vedtak(datoforrigevedtak=" + this.getDatoforrigevedtak() + ", eropprinneligvedtak=" + this.getEropprinneligvedtak() + ", erendringsvedtak=" + this.getErendringsvedtak() + ")";
    }
}
