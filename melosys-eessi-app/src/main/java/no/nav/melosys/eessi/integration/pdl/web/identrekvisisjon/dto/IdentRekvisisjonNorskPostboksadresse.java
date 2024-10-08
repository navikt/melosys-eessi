// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse.Postboksadresse;

public class IdentRekvisisjonNorskPostboksadresse extends Postboksadresse {
    private String postboks;
    private String postnummer;


    @java.lang.SuppressWarnings("all")
    public static class IdentRekvisisjonNorskPostboksadresseBuilder {
        @java.lang.SuppressWarnings("all")
        private String postboks;
        @java.lang.SuppressWarnings("all")
        private String postnummer;

        @java.lang.SuppressWarnings("all")
        IdentRekvisisjonNorskPostboksadresseBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonNorskPostboksadresse.IdentRekvisisjonNorskPostboksadresseBuilder postboks(final String postboks) {
            this.postboks = postboks;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonNorskPostboksadresse.IdentRekvisisjonNorskPostboksadresseBuilder postnummer(final String postnummer) {
            this.postnummer = postnummer;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonNorskPostboksadresse build() {
            return new IdentRekvisisjonNorskPostboksadresse(this.postboks, this.postnummer);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "IdentRekvisisjonNorskPostboksadresse.IdentRekvisisjonNorskPostboksadresseBuilder(postboks=" + this.postboks + ", postnummer=" + this.postnummer + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static IdentRekvisisjonNorskPostboksadresse.IdentRekvisisjonNorskPostboksadresseBuilder builder() {
        return new IdentRekvisisjonNorskPostboksadresse.IdentRekvisisjonNorskPostboksadresseBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public String getPostboks() {
        return this.postboks;
    }

    @java.lang.SuppressWarnings("all")
    public String getPostnummer() {
        return this.postnummer;
    }

    @java.lang.SuppressWarnings("all")
    public void setPostboks(final String postboks) {
        this.postboks = postboks;
    }

    @java.lang.SuppressWarnings("all")
    public void setPostnummer(final String postnummer) {
        this.postnummer = postnummer;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "IdentRekvisisjonNorskPostboksadresse(postboks=" + this.getPostboks() + ", postnummer=" + this.getPostnummer() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public IdentRekvisisjonNorskPostboksadresse() {
    }

    @java.lang.SuppressWarnings("all")
    public IdentRekvisisjonNorskPostboksadresse(final String postboks, final String postnummer) {
        this.postboks = postboks;
        this.postnummer = postnummer;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof IdentRekvisisjonNorskPostboksadresse)) return false;
        final IdentRekvisisjonNorskPostboksadresse other = (IdentRekvisisjonNorskPostboksadresse) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (!super.equals(o)) return false;
        final java.lang.Object this$postboks = this.getPostboks();
        final java.lang.Object other$postboks = other.getPostboks();
        if (this$postboks == null ? other$postboks != null : !this$postboks.equals(other$postboks)) return false;
        final java.lang.Object this$postnummer = this.getPostnummer();
        final java.lang.Object other$postnummer = other.getPostnummer();
        if (this$postnummer == null ? other$postnummer != null : !this$postnummer.equals(other$postnummer))
            return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof IdentRekvisisjonNorskPostboksadresse;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final java.lang.Object $postboks = this.getPostboks();
        result = result * PRIME + ($postboks == null ? 43 : $postboks.hashCode());
        final java.lang.Object $postnummer = this.getPostnummer();
        result = result * PRIME + ($postnummer == null ? 43 : $postnummer.hashCode());
        return result;
    }
}
