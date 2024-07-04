// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.service.eux;

public class BucSearch {
    private String fnr;
    private String fornavn;
    private String etternavn;
    private String foedselsdato;
    private String rinaSaksnummer;
    private String bucType;
    private String status;


    @java.lang.SuppressWarnings("all")
    public static class BucSearchBuilder {
        @java.lang.SuppressWarnings("all")
        private String fnr;
        @java.lang.SuppressWarnings("all")
        private String fornavn;
        @java.lang.SuppressWarnings("all")
        private String etternavn;
        @java.lang.SuppressWarnings("all")
        private String foedselsdato;
        @java.lang.SuppressWarnings("all")
        private String rinaSaksnummer;
        @java.lang.SuppressWarnings("all")
        private String bucType;
        @java.lang.SuppressWarnings("all")
        private String status;

        @java.lang.SuppressWarnings("all")
        BucSearchBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public BucSearch.BucSearchBuilder fnr(final String fnr) {
            this.fnr = fnr;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public BucSearch.BucSearchBuilder fornavn(final String fornavn) {
            this.fornavn = fornavn;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public BucSearch.BucSearchBuilder etternavn(final String etternavn) {
            this.etternavn = etternavn;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public BucSearch.BucSearchBuilder foedselsdato(final String foedselsdato) {
            this.foedselsdato = foedselsdato;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public BucSearch.BucSearchBuilder rinaSaksnummer(final String rinaSaksnummer) {
            this.rinaSaksnummer = rinaSaksnummer;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public BucSearch.BucSearchBuilder bucType(final String bucType) {
            this.bucType = bucType;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public BucSearch.BucSearchBuilder status(final String status) {
            this.status = status;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public BucSearch build() {
            return new BucSearch(this.fnr, this.fornavn, this.etternavn, this.foedselsdato, this.rinaSaksnummer, this.bucType, this.status);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "BucSearch.BucSearchBuilder(fnr=" + this.fnr + ", fornavn=" + this.fornavn + ", etternavn=" + this.etternavn + ", foedselsdato=" + this.foedselsdato + ", rinaSaksnummer=" + this.rinaSaksnummer + ", bucType=" + this.bucType + ", status=" + this.status + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static BucSearch.BucSearchBuilder builder() {
        return new BucSearch.BucSearchBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public String getFnr() {
        return this.fnr;
    }

    @java.lang.SuppressWarnings("all")
    public String getFornavn() {
        return this.fornavn;
    }

    @java.lang.SuppressWarnings("all")
    public String getEtternavn() {
        return this.etternavn;
    }

    @java.lang.SuppressWarnings("all")
    public String getFoedselsdato() {
        return this.foedselsdato;
    }

    @java.lang.SuppressWarnings("all")
    public String getRinaSaksnummer() {
        return this.rinaSaksnummer;
    }

    @java.lang.SuppressWarnings("all")
    public String getBucType() {
        return this.bucType;
    }

    @java.lang.SuppressWarnings("all")
    public String getStatus() {
        return this.status;
    }

    @java.lang.SuppressWarnings("all")
    public void setFnr(final String fnr) {
        this.fnr = fnr;
    }

    @java.lang.SuppressWarnings("all")
    public void setFornavn(final String fornavn) {
        this.fornavn = fornavn;
    }

    @java.lang.SuppressWarnings("all")
    public void setEtternavn(final String etternavn) {
        this.etternavn = etternavn;
    }

    @java.lang.SuppressWarnings("all")
    public void setFoedselsdato(final String foedselsdato) {
        this.foedselsdato = foedselsdato;
    }

    @java.lang.SuppressWarnings("all")
    public void setRinaSaksnummer(final String rinaSaksnummer) {
        this.rinaSaksnummer = rinaSaksnummer;
    }

    @java.lang.SuppressWarnings("all")
    public void setBucType(final String bucType) {
        this.bucType = bucType;
    }

    @java.lang.SuppressWarnings("all")
    public void setStatus(final String status) {
        this.status = status;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof BucSearch)) return false;
        final BucSearch other = (BucSearch) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$fnr = this.getFnr();
        final java.lang.Object other$fnr = other.getFnr();
        if (this$fnr == null ? other$fnr != null : !this$fnr.equals(other$fnr)) return false;
        final java.lang.Object this$fornavn = this.getFornavn();
        final java.lang.Object other$fornavn = other.getFornavn();
        if (this$fornavn == null ? other$fornavn != null : !this$fornavn.equals(other$fornavn)) return false;
        final java.lang.Object this$etternavn = this.getEtternavn();
        final java.lang.Object other$etternavn = other.getEtternavn();
        if (this$etternavn == null ? other$etternavn != null : !this$etternavn.equals(other$etternavn)) return false;
        final java.lang.Object this$foedselsdato = this.getFoedselsdato();
        final java.lang.Object other$foedselsdato = other.getFoedselsdato();
        if (this$foedselsdato == null ? other$foedselsdato != null : !this$foedselsdato.equals(other$foedselsdato)) return false;
        final java.lang.Object this$rinaSaksnummer = this.getRinaSaksnummer();
        final java.lang.Object other$rinaSaksnummer = other.getRinaSaksnummer();
        if (this$rinaSaksnummer == null ? other$rinaSaksnummer != null : !this$rinaSaksnummer.equals(other$rinaSaksnummer)) return false;
        final java.lang.Object this$bucType = this.getBucType();
        final java.lang.Object other$bucType = other.getBucType();
        if (this$bucType == null ? other$bucType != null : !this$bucType.equals(other$bucType)) return false;
        final java.lang.Object this$status = this.getStatus();
        final java.lang.Object other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof BucSearch;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $fnr = this.getFnr();
        result = result * PRIME + ($fnr == null ? 43 : $fnr.hashCode());
        final java.lang.Object $fornavn = this.getFornavn();
        result = result * PRIME + ($fornavn == null ? 43 : $fornavn.hashCode());
        final java.lang.Object $etternavn = this.getEtternavn();
        result = result * PRIME + ($etternavn == null ? 43 : $etternavn.hashCode());
        final java.lang.Object $foedselsdato = this.getFoedselsdato();
        result = result * PRIME + ($foedselsdato == null ? 43 : $foedselsdato.hashCode());
        final java.lang.Object $rinaSaksnummer = this.getRinaSaksnummer();
        result = result * PRIME + ($rinaSaksnummer == null ? 43 : $rinaSaksnummer.hashCode());
        final java.lang.Object $bucType = this.getBucType();
        result = result * PRIME + ($bucType == null ? 43 : $bucType.hashCode());
        final java.lang.Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 43 : $status.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "BucSearch(fnr=" + this.getFnr() + ", fornavn=" + this.getFornavn() + ", etternavn=" + this.getEtternavn() + ", foedselsdato=" + this.getFoedselsdato() + ", rinaSaksnummer=" + this.getRinaSaksnummer() + ", bucType=" + this.getBucType() + ", status=" + this.getStatus() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public BucSearch() {
    }

    @java.lang.SuppressWarnings("all")
    public BucSearch(final String fnr, final String fornavn, final String etternavn, final String foedselsdato, final String rinaSaksnummer, final String bucType, final String status) {
        this.fnr = fnr;
        this.fornavn = fornavn;
        this.etternavn = etternavn;
        this.foedselsdato = foedselsdato;
        this.rinaSaksnummer = rinaSaksnummer;
        this.bucType = bucType;
        this.status = status;
    }
}
