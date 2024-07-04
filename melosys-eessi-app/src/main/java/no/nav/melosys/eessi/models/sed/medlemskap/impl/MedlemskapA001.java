// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.nav.*;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MedlemskapA001 implements Medlemskap {
    private Unntak unntak;
    private Vertsland vertsland;
    private Fastperiode soeknadsperiode;
    private List<Periode> tidligereperiode;
    private List<Land> naavaerendemedlemskap; // Landkode
    private List<Land> forespurtmedlemskap; // Landkode
    private Anmodning anmodning;
    private List<ForrigeSoeknad> forrigesoeknad;

    @java.lang.SuppressWarnings("all")
    public MedlemskapA001() {
    }

    @java.lang.SuppressWarnings("all")
    public Unntak getUnntak() {
        return this.unntak;
    }

    @java.lang.SuppressWarnings("all")
    public Vertsland getVertsland() {
        return this.vertsland;
    }

    @java.lang.SuppressWarnings("all")
    public Fastperiode getSoeknadsperiode() {
        return this.soeknadsperiode;
    }

    @java.lang.SuppressWarnings("all")
    public List<Periode> getTidligereperiode() {
        return this.tidligereperiode;
    }

    @java.lang.SuppressWarnings("all")
    public List<Land> getNaavaerendemedlemskap() {
        return this.naavaerendemedlemskap;
    }

    @java.lang.SuppressWarnings("all")
    public List<Land> getForespurtmedlemskap() {
        return this.forespurtmedlemskap;
    }

    @java.lang.SuppressWarnings("all")
    public Anmodning getAnmodning() {
        return this.anmodning;
    }

    @java.lang.SuppressWarnings("all")
    public List<ForrigeSoeknad> getForrigesoeknad() {
        return this.forrigesoeknad;
    }

    @java.lang.SuppressWarnings("all")
    public void setUnntak(final Unntak unntak) {
        this.unntak = unntak;
    }

    @java.lang.SuppressWarnings("all")
    public void setVertsland(final Vertsland vertsland) {
        this.vertsland = vertsland;
    }

    @java.lang.SuppressWarnings("all")
    public void setSoeknadsperiode(final Fastperiode soeknadsperiode) {
        this.soeknadsperiode = soeknadsperiode;
    }

    @java.lang.SuppressWarnings("all")
    public void setTidligereperiode(final List<Periode> tidligereperiode) {
        this.tidligereperiode = tidligereperiode;
    }

    @java.lang.SuppressWarnings("all")
    public void setNaavaerendemedlemskap(final List<Land> naavaerendemedlemskap) {
        this.naavaerendemedlemskap = naavaerendemedlemskap;
    }

    @java.lang.SuppressWarnings("all")
    public void setForespurtmedlemskap(final List<Land> forespurtmedlemskap) {
        this.forespurtmedlemskap = forespurtmedlemskap;
    }

    @java.lang.SuppressWarnings("all")
    public void setAnmodning(final Anmodning anmodning) {
        this.anmodning = anmodning;
    }

    @java.lang.SuppressWarnings("all")
    public void setForrigesoeknad(final List<ForrigeSoeknad> forrigesoeknad) {
        this.forrigesoeknad = forrigesoeknad;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof MedlemskapA001)) return false;
        final MedlemskapA001 other = (MedlemskapA001) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$unntak = this.getUnntak();
        final java.lang.Object other$unntak = other.getUnntak();
        if (this$unntak == null ? other$unntak != null : !this$unntak.equals(other$unntak)) return false;
        final java.lang.Object this$vertsland = this.getVertsland();
        final java.lang.Object other$vertsland = other.getVertsland();
        if (this$vertsland == null ? other$vertsland != null : !this$vertsland.equals(other$vertsland)) return false;
        final java.lang.Object this$soeknadsperiode = this.getSoeknadsperiode();
        final java.lang.Object other$soeknadsperiode = other.getSoeknadsperiode();
        if (this$soeknadsperiode == null ? other$soeknadsperiode != null : !this$soeknadsperiode.equals(other$soeknadsperiode)) return false;
        final java.lang.Object this$tidligereperiode = this.getTidligereperiode();
        final java.lang.Object other$tidligereperiode = other.getTidligereperiode();
        if (this$tidligereperiode == null ? other$tidligereperiode != null : !this$tidligereperiode.equals(other$tidligereperiode)) return false;
        final java.lang.Object this$naavaerendemedlemskap = this.getNaavaerendemedlemskap();
        final java.lang.Object other$naavaerendemedlemskap = other.getNaavaerendemedlemskap();
        if (this$naavaerendemedlemskap == null ? other$naavaerendemedlemskap != null : !this$naavaerendemedlemskap.equals(other$naavaerendemedlemskap)) return false;
        final java.lang.Object this$forespurtmedlemskap = this.getForespurtmedlemskap();
        final java.lang.Object other$forespurtmedlemskap = other.getForespurtmedlemskap();
        if (this$forespurtmedlemskap == null ? other$forespurtmedlemskap != null : !this$forespurtmedlemskap.equals(other$forespurtmedlemskap)) return false;
        final java.lang.Object this$anmodning = this.getAnmodning();
        final java.lang.Object other$anmodning = other.getAnmodning();
        if (this$anmodning == null ? other$anmodning != null : !this$anmodning.equals(other$anmodning)) return false;
        final java.lang.Object this$forrigesoeknad = this.getForrigesoeknad();
        final java.lang.Object other$forrigesoeknad = other.getForrigesoeknad();
        if (this$forrigesoeknad == null ? other$forrigesoeknad != null : !this$forrigesoeknad.equals(other$forrigesoeknad)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof MedlemskapA001;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $unntak = this.getUnntak();
        result = result * PRIME + ($unntak == null ? 43 : $unntak.hashCode());
        final java.lang.Object $vertsland = this.getVertsland();
        result = result * PRIME + ($vertsland == null ? 43 : $vertsland.hashCode());
        final java.lang.Object $soeknadsperiode = this.getSoeknadsperiode();
        result = result * PRIME + ($soeknadsperiode == null ? 43 : $soeknadsperiode.hashCode());
        final java.lang.Object $tidligereperiode = this.getTidligereperiode();
        result = result * PRIME + ($tidligereperiode == null ? 43 : $tidligereperiode.hashCode());
        final java.lang.Object $naavaerendemedlemskap = this.getNaavaerendemedlemskap();
        result = result * PRIME + ($naavaerendemedlemskap == null ? 43 : $naavaerendemedlemskap.hashCode());
        final java.lang.Object $forespurtmedlemskap = this.getForespurtmedlemskap();
        result = result * PRIME + ($forespurtmedlemskap == null ? 43 : $forespurtmedlemskap.hashCode());
        final java.lang.Object $anmodning = this.getAnmodning();
        result = result * PRIME + ($anmodning == null ? 43 : $anmodning.hashCode());
        final java.lang.Object $forrigesoeknad = this.getForrigesoeknad();
        result = result * PRIME + ($forrigesoeknad == null ? 43 : $forrigesoeknad.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "MedlemskapA001(unntak=" + this.getUnntak() + ", vertsland=" + this.getVertsland() + ", soeknadsperiode=" + this.getSoeknadsperiode() + ", tidligereperiode=" + this.getTidligereperiode() + ", naavaerendemedlemskap=" + this.getNaavaerendemedlemskap() + ", forespurtmedlemskap=" + this.getForespurtmedlemskap() + ", anmodning=" + this.getAnmodning() + ", forrigesoeknad=" + this.getForrigesoeknad() + ")";
    }
}
