// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.controller.dto;

public class BucOgSedOpprettetDto {
    private String rinaSaksnummer;
    private String rinaUrl;

    @java.lang.SuppressWarnings("all")
    BucOgSedOpprettetDto(final String rinaSaksnummer, final String rinaUrl) {
        this.rinaSaksnummer = rinaSaksnummer;
        this.rinaUrl = rinaUrl;
    }


    @java.lang.SuppressWarnings("all")
    public static class BucOgSedOpprettetDtoBuilder {
        @java.lang.SuppressWarnings("all")
        private String rinaSaksnummer;
        @java.lang.SuppressWarnings("all")
        private String rinaUrl;

        @java.lang.SuppressWarnings("all")
        BucOgSedOpprettetDtoBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public BucOgSedOpprettetDto.BucOgSedOpprettetDtoBuilder rinaSaksnummer(final String rinaSaksnummer) {
            this.rinaSaksnummer = rinaSaksnummer;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public BucOgSedOpprettetDto.BucOgSedOpprettetDtoBuilder rinaUrl(final String rinaUrl) {
            this.rinaUrl = rinaUrl;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public BucOgSedOpprettetDto build() {
            return new BucOgSedOpprettetDto(this.rinaSaksnummer, this.rinaUrl);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "BucOgSedOpprettetDto.BucOgSedOpprettetDtoBuilder(rinaSaksnummer=" + this.rinaSaksnummer + ", rinaUrl=" + this.rinaUrl + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static BucOgSedOpprettetDto.BucOgSedOpprettetDtoBuilder builder() {
        return new BucOgSedOpprettetDto.BucOgSedOpprettetDtoBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public String getRinaSaksnummer() {
        return this.rinaSaksnummer;
    }

    @java.lang.SuppressWarnings("all")
    public String getRinaUrl() {
        return this.rinaUrl;
    }

    @java.lang.SuppressWarnings("all")
    public void setRinaSaksnummer(final String rinaSaksnummer) {
        this.rinaSaksnummer = rinaSaksnummer;
    }

    @java.lang.SuppressWarnings("all")
    public void setRinaUrl(final String rinaUrl) {
        this.rinaUrl = rinaUrl;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof BucOgSedOpprettetDto)) return false;
        final BucOgSedOpprettetDto other = (BucOgSedOpprettetDto) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$rinaSaksnummer = this.getRinaSaksnummer();
        final java.lang.Object other$rinaSaksnummer = other.getRinaSaksnummer();
        if (this$rinaSaksnummer == null ? other$rinaSaksnummer != null : !this$rinaSaksnummer.equals(other$rinaSaksnummer))
            return false;
        final java.lang.Object this$rinaUrl = this.getRinaUrl();
        final java.lang.Object other$rinaUrl = other.getRinaUrl();
        if (this$rinaUrl == null ? other$rinaUrl != null : !this$rinaUrl.equals(other$rinaUrl)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof BucOgSedOpprettetDto;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $rinaSaksnummer = this.getRinaSaksnummer();
        result = result * PRIME + ($rinaSaksnummer == null ? 43 : $rinaSaksnummer.hashCode());
        final java.lang.Object $rinaUrl = this.getRinaUrl();
        result = result * PRIME + ($rinaUrl == null ? 43 : $rinaUrl.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "BucOgSedOpprettetDto(rinaSaksnummer=" + this.getRinaSaksnummer() + ", rinaUrl=" + this.getRinaUrl() + ")";
    }
}
