// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

public class IdentRekvisisjonBostedsadresse {
    private String gyldigFraOgMed;
    private IdentRekvisisjonUtenlandskVegadresse utenlandskVegadresse;


    @java.lang.SuppressWarnings("all")
    public static class IdentRekvisisjonBostedsadresseBuilder {
        @java.lang.SuppressWarnings("all")
        private String gyldigFraOgMed;
        @java.lang.SuppressWarnings("all")
        private IdentRekvisisjonUtenlandskVegadresse utenlandskVegadresse;

        @java.lang.SuppressWarnings("all")
        IdentRekvisisjonBostedsadresseBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonBostedsadresse.IdentRekvisisjonBostedsadresseBuilder gyldigFraOgMed(final String gyldigFraOgMed) {
            this.gyldigFraOgMed = gyldigFraOgMed;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonBostedsadresse.IdentRekvisisjonBostedsadresseBuilder utenlandskVegadresse(final IdentRekvisisjonUtenlandskVegadresse utenlandskVegadresse) {
            this.utenlandskVegadresse = utenlandskVegadresse;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonBostedsadresse build() {
            return new IdentRekvisisjonBostedsadresse(this.gyldigFraOgMed, this.utenlandskVegadresse);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "IdentRekvisisjonBostedsadresse.IdentRekvisisjonBostedsadresseBuilder(gyldigFraOgMed=" + this.gyldigFraOgMed + ", utenlandskVegadresse=" + this.utenlandskVegadresse + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static IdentRekvisisjonBostedsadresse.IdentRekvisisjonBostedsadresseBuilder builder() {
        return new IdentRekvisisjonBostedsadresse.IdentRekvisisjonBostedsadresseBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public String getGyldigFraOgMed() {
        return this.gyldigFraOgMed;
    }

    @java.lang.SuppressWarnings("all")
    public IdentRekvisisjonUtenlandskVegadresse getUtenlandskVegadresse() {
        return this.utenlandskVegadresse;
    }

    @java.lang.SuppressWarnings("all")
    public void setGyldigFraOgMed(final String gyldigFraOgMed) {
        this.gyldigFraOgMed = gyldigFraOgMed;
    }

    @java.lang.SuppressWarnings("all")
    public void setUtenlandskVegadresse(final IdentRekvisisjonUtenlandskVegadresse utenlandskVegadresse) {
        this.utenlandskVegadresse = utenlandskVegadresse;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof IdentRekvisisjonBostedsadresse)) return false;
        final IdentRekvisisjonBostedsadresse other = (IdentRekvisisjonBostedsadresse) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$gyldigFraOgMed = this.getGyldigFraOgMed();
        final java.lang.Object other$gyldigFraOgMed = other.getGyldigFraOgMed();
        if (this$gyldigFraOgMed == null ? other$gyldigFraOgMed != null : !this$gyldigFraOgMed.equals(other$gyldigFraOgMed))
            return false;
        final java.lang.Object this$utenlandskVegadresse = this.getUtenlandskVegadresse();
        final java.lang.Object other$utenlandskVegadresse = other.getUtenlandskVegadresse();
        if (this$utenlandskVegadresse == null ? other$utenlandskVegadresse != null : !this$utenlandskVegadresse.equals(other$utenlandskVegadresse))
            return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof IdentRekvisisjonBostedsadresse;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $gyldigFraOgMed = this.getGyldigFraOgMed();
        result = result * PRIME + ($gyldigFraOgMed == null ? 43 : $gyldigFraOgMed.hashCode());
        final java.lang.Object $utenlandskVegadresse = this.getUtenlandskVegadresse();
        result = result * PRIME + ($utenlandskVegadresse == null ? 43 : $utenlandskVegadresse.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "IdentRekvisisjonBostedsadresse(gyldigFraOgMed=" + this.getGyldigFraOgMed() + ", utenlandskVegadresse=" + this.getUtenlandskVegadresse() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public IdentRekvisisjonBostedsadresse() {
    }

    @java.lang.SuppressWarnings("all")
    public IdentRekvisisjonBostedsadresse(final String gyldigFraOgMed, final IdentRekvisisjonUtenlandskVegadresse utenlandskVegadresse) {
        this.gyldigFraOgMed = gyldigFraOgMed;
        this.utenlandskVegadresse = utenlandskVegadresse;
    }
}
