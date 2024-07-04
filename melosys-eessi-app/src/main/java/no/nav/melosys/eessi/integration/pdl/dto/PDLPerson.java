// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.pdl.dto;

import java.util.Collection;

public class PDLPerson {
    private Collection<PDLNavn> navn;
    private Collection<PDLFoedsel> foedsel;
    private Collection<PDLStatsborgerskap> statsborgerskap;
    private Collection<PDLFolkeregisterPersonstatus> folkeregisterpersonstatus;
    private Collection<PDLUtenlandskIdentifikator> utenlandskIdentifikasjonsnummer;
    private Collection<PDLKjoenn> kjoenn;

    @java.lang.SuppressWarnings("all")
    public PDLPerson() {
    }

    @java.lang.SuppressWarnings("all")
    public Collection<PDLNavn> getNavn() {
        return this.navn;
    }

    @java.lang.SuppressWarnings("all")
    public Collection<PDLFoedsel> getFoedsel() {
        return this.foedsel;
    }

    @java.lang.SuppressWarnings("all")
    public Collection<PDLStatsborgerskap> getStatsborgerskap() {
        return this.statsborgerskap;
    }

    @java.lang.SuppressWarnings("all")
    public Collection<PDLFolkeregisterPersonstatus> getFolkeregisterpersonstatus() {
        return this.folkeregisterpersonstatus;
    }

    @java.lang.SuppressWarnings("all")
    public Collection<PDLUtenlandskIdentifikator> getUtenlandskIdentifikasjonsnummer() {
        return this.utenlandskIdentifikasjonsnummer;
    }

    @java.lang.SuppressWarnings("all")
    public Collection<PDLKjoenn> getKjoenn() {
        return this.kjoenn;
    }

    @java.lang.SuppressWarnings("all")
    public void setNavn(final Collection<PDLNavn> navn) {
        this.navn = navn;
    }

    @java.lang.SuppressWarnings("all")
    public void setFoedsel(final Collection<PDLFoedsel> foedsel) {
        this.foedsel = foedsel;
    }

    @java.lang.SuppressWarnings("all")
    public void setStatsborgerskap(final Collection<PDLStatsborgerskap> statsborgerskap) {
        this.statsborgerskap = statsborgerskap;
    }

    @java.lang.SuppressWarnings("all")
    public void setFolkeregisterpersonstatus(final Collection<PDLFolkeregisterPersonstatus> folkeregisterpersonstatus) {
        this.folkeregisterpersonstatus = folkeregisterpersonstatus;
    }

    @java.lang.SuppressWarnings("all")
    public void setUtenlandskIdentifikasjonsnummer(final Collection<PDLUtenlandskIdentifikator> utenlandskIdentifikasjonsnummer) {
        this.utenlandskIdentifikasjonsnummer = utenlandskIdentifikasjonsnummer;
    }

    @java.lang.SuppressWarnings("all")
    public void setKjoenn(final Collection<PDLKjoenn> kjoenn) {
        this.kjoenn = kjoenn;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof PDLPerson)) return false;
        final PDLPerson other = (PDLPerson) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$navn = this.getNavn();
        final java.lang.Object other$navn = other.getNavn();
        if (this$navn == null ? other$navn != null : !this$navn.equals(other$navn)) return false;
        final java.lang.Object this$foedsel = this.getFoedsel();
        final java.lang.Object other$foedsel = other.getFoedsel();
        if (this$foedsel == null ? other$foedsel != null : !this$foedsel.equals(other$foedsel)) return false;
        final java.lang.Object this$statsborgerskap = this.getStatsborgerskap();
        final java.lang.Object other$statsborgerskap = other.getStatsborgerskap();
        if (this$statsborgerskap == null ? other$statsborgerskap != null : !this$statsborgerskap.equals(other$statsborgerskap)) return false;
        final java.lang.Object this$folkeregisterpersonstatus = this.getFolkeregisterpersonstatus();
        final java.lang.Object other$folkeregisterpersonstatus = other.getFolkeregisterpersonstatus();
        if (this$folkeregisterpersonstatus == null ? other$folkeregisterpersonstatus != null : !this$folkeregisterpersonstatus.equals(other$folkeregisterpersonstatus)) return false;
        final java.lang.Object this$utenlandskIdentifikasjonsnummer = this.getUtenlandskIdentifikasjonsnummer();
        final java.lang.Object other$utenlandskIdentifikasjonsnummer = other.getUtenlandskIdentifikasjonsnummer();
        if (this$utenlandskIdentifikasjonsnummer == null ? other$utenlandskIdentifikasjonsnummer != null : !this$utenlandskIdentifikasjonsnummer.equals(other$utenlandskIdentifikasjonsnummer)) return false;
        final java.lang.Object this$kjoenn = this.getKjoenn();
        final java.lang.Object other$kjoenn = other.getKjoenn();
        if (this$kjoenn == null ? other$kjoenn != null : !this$kjoenn.equals(other$kjoenn)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof PDLPerson;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $navn = this.getNavn();
        result = result * PRIME + ($navn == null ? 43 : $navn.hashCode());
        final java.lang.Object $foedsel = this.getFoedsel();
        result = result * PRIME + ($foedsel == null ? 43 : $foedsel.hashCode());
        final java.lang.Object $statsborgerskap = this.getStatsborgerskap();
        result = result * PRIME + ($statsborgerskap == null ? 43 : $statsborgerskap.hashCode());
        final java.lang.Object $folkeregisterpersonstatus = this.getFolkeregisterpersonstatus();
        result = result * PRIME + ($folkeregisterpersonstatus == null ? 43 : $folkeregisterpersonstatus.hashCode());
        final java.lang.Object $utenlandskIdentifikasjonsnummer = this.getUtenlandskIdentifikasjonsnummer();
        result = result * PRIME + ($utenlandskIdentifikasjonsnummer == null ? 43 : $utenlandskIdentifikasjonsnummer.hashCode());
        final java.lang.Object $kjoenn = this.getKjoenn();
        result = result * PRIME + ($kjoenn == null ? 43 : $kjoenn.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "PDLPerson(navn=" + this.getNavn() + ", foedsel=" + this.getFoedsel() + ", statsborgerskap=" + this.getStatsborgerskap() + ", folkeregisterpersonstatus=" + this.getFolkeregisterpersonstatus() + ", utenlandskIdentifikasjonsnummer=" + this.getUtenlandskIdentifikasjonsnummer() + ", kjoenn=" + this.getKjoenn() + ")";
    }
}
