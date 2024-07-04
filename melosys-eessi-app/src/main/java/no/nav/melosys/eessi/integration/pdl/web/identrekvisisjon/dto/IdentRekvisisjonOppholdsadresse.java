// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

public class IdentRekvisisjonOppholdsadresse {
    private String gyldigFraOgMed;
    private IdentRekvisisjonNorskVegadresse norskVegadresse;
    private IdentRekvisisjonUtenlandskVegadresse utenlandskVegadresse;


    @java.lang.SuppressWarnings("all")
    public static class IdentRekvisisjonOppholdsadresseBuilder {
        @java.lang.SuppressWarnings("all")
        private String gyldigFraOgMed;
        @java.lang.SuppressWarnings("all")
        private IdentRekvisisjonNorskVegadresse norskVegadresse;
        @java.lang.SuppressWarnings("all")
        private IdentRekvisisjonUtenlandskVegadresse utenlandskVegadresse;

        @java.lang.SuppressWarnings("all")
        IdentRekvisisjonOppholdsadresseBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonOppholdsadresse.IdentRekvisisjonOppholdsadresseBuilder gyldigFraOgMed(final String gyldigFraOgMed) {
            this.gyldigFraOgMed = gyldigFraOgMed;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonOppholdsadresse.IdentRekvisisjonOppholdsadresseBuilder norskVegadresse(final IdentRekvisisjonNorskVegadresse norskVegadresse) {
            this.norskVegadresse = norskVegadresse;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonOppholdsadresse.IdentRekvisisjonOppholdsadresseBuilder utenlandskVegadresse(final IdentRekvisisjonUtenlandskVegadresse utenlandskVegadresse) {
            this.utenlandskVegadresse = utenlandskVegadresse;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public IdentRekvisisjonOppholdsadresse build() {
            return new IdentRekvisisjonOppholdsadresse(this.gyldigFraOgMed, this.norskVegadresse, this.utenlandskVegadresse);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "IdentRekvisisjonOppholdsadresse.IdentRekvisisjonOppholdsadresseBuilder(gyldigFraOgMed=" + this.gyldigFraOgMed + ", norskVegadresse=" + this.norskVegadresse + ", utenlandskVegadresse=" + this.utenlandskVegadresse + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static IdentRekvisisjonOppholdsadresse.IdentRekvisisjonOppholdsadresseBuilder builder() {
        return new IdentRekvisisjonOppholdsadresse.IdentRekvisisjonOppholdsadresseBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public String getGyldigFraOgMed() {
        return this.gyldigFraOgMed;
    }

    @java.lang.SuppressWarnings("all")
    public IdentRekvisisjonNorskVegadresse getNorskVegadresse() {
        return this.norskVegadresse;
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
    public void setNorskVegadresse(final IdentRekvisisjonNorskVegadresse norskVegadresse) {
        this.norskVegadresse = norskVegadresse;
    }

    @java.lang.SuppressWarnings("all")
    public void setUtenlandskVegadresse(final IdentRekvisisjonUtenlandskVegadresse utenlandskVegadresse) {
        this.utenlandskVegadresse = utenlandskVegadresse;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof IdentRekvisisjonOppholdsadresse)) return false;
        final IdentRekvisisjonOppholdsadresse other = (IdentRekvisisjonOppholdsadresse) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$gyldigFraOgMed = this.getGyldigFraOgMed();
        final java.lang.Object other$gyldigFraOgMed = other.getGyldigFraOgMed();
        if (this$gyldigFraOgMed == null ? other$gyldigFraOgMed != null : !this$gyldigFraOgMed.equals(other$gyldigFraOgMed))
            return false;
        final java.lang.Object this$norskVegadresse = this.getNorskVegadresse();
        final java.lang.Object other$norskVegadresse = other.getNorskVegadresse();
        if (this$norskVegadresse == null ? other$norskVegadresse != null : !this$norskVegadresse.equals(other$norskVegadresse))
            return false;
        final java.lang.Object this$utenlandskVegadresse = this.getUtenlandskVegadresse();
        final java.lang.Object other$utenlandskVegadresse = other.getUtenlandskVegadresse();
        if (this$utenlandskVegadresse == null ? other$utenlandskVegadresse != null : !this$utenlandskVegadresse.equals(other$utenlandskVegadresse))
            return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof IdentRekvisisjonOppholdsadresse;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $gyldigFraOgMed = this.getGyldigFraOgMed();
        result = result * PRIME + ($gyldigFraOgMed == null ? 43 : $gyldigFraOgMed.hashCode());
        final java.lang.Object $norskVegadresse = this.getNorskVegadresse();
        result = result * PRIME + ($norskVegadresse == null ? 43 : $norskVegadresse.hashCode());
        final java.lang.Object $utenlandskVegadresse = this.getUtenlandskVegadresse();
        result = result * PRIME + ($utenlandskVegadresse == null ? 43 : $utenlandskVegadresse.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "IdentRekvisisjonOppholdsadresse(gyldigFraOgMed=" + this.getGyldigFraOgMed() + ", norskVegadresse=" + this.getNorskVegadresse() + ", utenlandskVegadresse=" + this.getUtenlandskVegadresse() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public IdentRekvisisjonOppholdsadresse() {
    }

    @java.lang.SuppressWarnings("all")
    public IdentRekvisisjonOppholdsadresse(final String gyldigFraOgMed, final IdentRekvisisjonNorskVegadresse norskVegadresse, final IdentRekvisisjonUtenlandskVegadresse utenlandskVegadresse) {
        this.gyldigFraOgMed = gyldigFraOgMed;
        this.norskVegadresse = norskVegadresse;
        this.utenlandskVegadresse = utenlandskVegadresse;
    }
}
