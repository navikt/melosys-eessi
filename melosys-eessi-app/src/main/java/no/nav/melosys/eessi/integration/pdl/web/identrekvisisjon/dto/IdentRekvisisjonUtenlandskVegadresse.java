// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse.Vegadresse;

public class IdentRekvisisjonUtenlandskVegadresse extends Vegadresse {
    private String adressenavnNummer;
    private String bygningEtasjeLeilighet;
    private String postkode;
    private String bySted;
    private String regionDistriktOmraade;
    private String landkode;


    @java.lang.SuppressWarnings("all")
    public static class IdentRekvisisjonUtenlandskVegadresseBuilder {
        @java.lang.SuppressWarnings("all")
        private String adressenavnNummer;
        @java.lang.SuppressWarnings("all")
        private String bygningEtasjeLeilighet;
        @java.lang.SuppressWarnings("all")
        private String postkode;
        @java.lang.SuppressWarnings("all")
        private String bySted;
        @java.lang.SuppressWarnings("all")
        private String regionDistriktOmraade;
        @java.lang.SuppressWarnings("all")
        private String landkode;

        @java.lang.SuppressWarnings("all")
        IdentRekvisisjonUtenlandskVegadresseBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonUtenlandskVegadresse.IdentRekvisisjonUtenlandskVegadresseBuilder adressenavnNummer(final String adressenavnNummer) {
            this.adressenavnNummer = adressenavnNummer;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonUtenlandskVegadresse.IdentRekvisisjonUtenlandskVegadresseBuilder bygningEtasjeLeilighet(final String bygningEtasjeLeilighet) {
            this.bygningEtasjeLeilighet = bygningEtasjeLeilighet;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonUtenlandskVegadresse.IdentRekvisisjonUtenlandskVegadresseBuilder postkode(final String postkode) {
            this.postkode = postkode;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonUtenlandskVegadresse.IdentRekvisisjonUtenlandskVegadresseBuilder bySted(final String bySted) {
            this.bySted = bySted;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonUtenlandskVegadresse.IdentRekvisisjonUtenlandskVegadresseBuilder regionDistriktOmraade(final String regionDistriktOmraade) {
            this.regionDistriktOmraade = regionDistriktOmraade;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonUtenlandskVegadresse.IdentRekvisisjonUtenlandskVegadresseBuilder landkode(final String landkode) {
            this.landkode = landkode;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonUtenlandskVegadresse build() {
            return new IdentRekvisisjonUtenlandskVegadresse(this.adressenavnNummer, this.bygningEtasjeLeilighet, this.postkode, this.bySted, this.regionDistriktOmraade, this.landkode);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "IdentRekvisisjonUtenlandskVegadresse.IdentRekvisisjonUtenlandskVegadresseBuilder(adressenavnNummer=" + this.adressenavnNummer + ", bygningEtasjeLeilighet=" + this.bygningEtasjeLeilighet + ", postkode=" + this.postkode + ", bySted=" + this.bySted + ", regionDistriktOmraade=" + this.regionDistriktOmraade + ", landkode=" + this.landkode + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static IdentRekvisisjonUtenlandskVegadresse.IdentRekvisisjonUtenlandskVegadresseBuilder builder() {
        return new IdentRekvisisjonUtenlandskVegadresse.IdentRekvisisjonUtenlandskVegadresseBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public String getAdressenavnNummer() {
        return this.adressenavnNummer;
    }

    @java.lang.SuppressWarnings("all")
    public String getBygningEtasjeLeilighet() {
        return this.bygningEtasjeLeilighet;
    }

    @java.lang.SuppressWarnings("all")
    public String getPostkode() {
        return this.postkode;
    }

    @java.lang.SuppressWarnings("all")
    public String getBySted() {
        return this.bySted;
    }

    @java.lang.SuppressWarnings("all")
    public String getRegionDistriktOmraade() {
        return this.regionDistriktOmraade;
    }

    @java.lang.SuppressWarnings("all")
    public String getLandkode() {
        return this.landkode;
    }

    @java.lang.SuppressWarnings("all")
    public void setAdressenavnNummer(final String adressenavnNummer) {
        this.adressenavnNummer = adressenavnNummer;
    }

    @java.lang.SuppressWarnings("all")
    public void setBygningEtasjeLeilighet(final String bygningEtasjeLeilighet) {
        this.bygningEtasjeLeilighet = bygningEtasjeLeilighet;
    }

    @java.lang.SuppressWarnings("all")
    public void setPostkode(final String postkode) {
        this.postkode = postkode;
    }

    @java.lang.SuppressWarnings("all")
    public void setBySted(final String bySted) {
        this.bySted = bySted;
    }

    @java.lang.SuppressWarnings("all")
    public void setRegionDistriktOmraade(final String regionDistriktOmraade) {
        this.regionDistriktOmraade = regionDistriktOmraade;
    }

    @java.lang.SuppressWarnings("all")
    public void setLandkode(final String landkode) {
        this.landkode = landkode;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "IdentRekvisisjonUtenlandskVegadresse(adressenavnNummer=" + this.getAdressenavnNummer() + ", bygningEtasjeLeilighet=" + this.getBygningEtasjeLeilighet() + ", postkode=" + this.getPostkode() + ", bySted=" + this.getBySted() + ", regionDistriktOmraade=" + this.getRegionDistriktOmraade() + ", landkode=" + this.getLandkode() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public IdentRekvisisjonUtenlandskVegadresse() {
    }

    @java.lang.SuppressWarnings("all")
    public IdentRekvisisjonUtenlandskVegadresse(final String adressenavnNummer, final String bygningEtasjeLeilighet, final String postkode, final String bySted, final String regionDistriktOmraade, final String landkode) {
        this.adressenavnNummer = adressenavnNummer;
        this.bygningEtasjeLeilighet = bygningEtasjeLeilighet;
        this.postkode = postkode;
        this.bySted = bySted;
        this.regionDistriktOmraade = regionDistriktOmraade;
        this.landkode = landkode;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof IdentRekvisisjonUtenlandskVegadresse)) return false;
        final IdentRekvisisjonUtenlandskVegadresse other = (IdentRekvisisjonUtenlandskVegadresse) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (!super.equals(o)) return false;
        final java.lang.Object this$adressenavnNummer = this.getAdressenavnNummer();
        final java.lang.Object other$adressenavnNummer = other.getAdressenavnNummer();
        if (this$adressenavnNummer == null ? other$adressenavnNummer != null : !this$adressenavnNummer.equals(other$adressenavnNummer))
            return false;
        final java.lang.Object this$bygningEtasjeLeilighet = this.getBygningEtasjeLeilighet();
        final java.lang.Object other$bygningEtasjeLeilighet = other.getBygningEtasjeLeilighet();
        if (this$bygningEtasjeLeilighet == null ? other$bygningEtasjeLeilighet != null : !this$bygningEtasjeLeilighet.equals(other$bygningEtasjeLeilighet))
            return false;
        final java.lang.Object this$postkode = this.getPostkode();
        final java.lang.Object other$postkode = other.getPostkode();
        if (this$postkode == null ? other$postkode != null : !this$postkode.equals(other$postkode)) return false;
        final java.lang.Object this$bySted = this.getBySted();
        final java.lang.Object other$bySted = other.getBySted();
        if (this$bySted == null ? other$bySted != null : !this$bySted.equals(other$bySted)) return false;
        final java.lang.Object this$regionDistriktOmraade = this.getRegionDistriktOmraade();
        final java.lang.Object other$regionDistriktOmraade = other.getRegionDistriktOmraade();
        if (this$regionDistriktOmraade == null ? other$regionDistriktOmraade != null : !this$regionDistriktOmraade.equals(other$regionDistriktOmraade))
            return false;
        final java.lang.Object this$landkode = this.getLandkode();
        final java.lang.Object other$landkode = other.getLandkode();
        if (this$landkode == null ? other$landkode != null : !this$landkode.equals(other$landkode)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof IdentRekvisisjonUtenlandskVegadresse;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final java.lang.Object $adressenavnNummer = this.getAdressenavnNummer();
        result = result * PRIME + ($adressenavnNummer == null ? 43 : $adressenavnNummer.hashCode());
        final java.lang.Object $bygningEtasjeLeilighet = this.getBygningEtasjeLeilighet();
        result = result * PRIME + ($bygningEtasjeLeilighet == null ? 43 : $bygningEtasjeLeilighet.hashCode());
        final java.lang.Object $postkode = this.getPostkode();
        result = result * PRIME + ($postkode == null ? 43 : $postkode.hashCode());
        final java.lang.Object $bySted = this.getBySted();
        result = result * PRIME + ($bySted == null ? 43 : $bySted.hashCode());
        final java.lang.Object $regionDistriktOmraade = this.getRegionDistriktOmraade();
        result = result * PRIME + ($regionDistriktOmraade == null ? 43 : $regionDistriktOmraade.hashCode());
        final java.lang.Object $landkode = this.getLandkode();
        result = result * PRIME + ($landkode == null ? 43 : $landkode.hashCode());
        return result;
    }
}
