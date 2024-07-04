// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.controller.dto;

import no.nav.melosys.eessi.models.buc.Document;

public class SedinfoDto {
    private String bucId;
    private String sedId;
    private Long opprettetDato;
    private Long sistOppdatert;
    private String sedType;
    private String status;
    private String rinaUrl;

    public static SedinfoDto av(Document document, String bucId, String rinaSedUrl) {
        return SedinfoDto.builder().bucId(bucId).sedId(document.getId()).sedType(document.getType()).opprettetDato(document.getCreationDate().toInstant().toEpochMilli()).sistOppdatert(document.getLastUpdate().toInstant().toEpochMilli()).status(tilNorskStatusEllerTomString(document.getStatus())).rinaUrl(rinaSedUrl).build();
    }

    private static String tilNorskStatusEllerTomString(String status) {
        SedStatus sedStatus = SedStatus.fraEngelskStatus(status);
        if (sedStatus == null) {
            return "";
        }
        return sedStatus.getNorskStatus();
    }


    @java.lang.SuppressWarnings("all")
    public static class SedinfoDtoBuilder {
        @java.lang.SuppressWarnings("all")
        private String bucId;
        @java.lang.SuppressWarnings("all")
        private String sedId;
        @java.lang.SuppressWarnings("all")
        private Long opprettetDato;
        @java.lang.SuppressWarnings("all")
        private Long sistOppdatert;
        @java.lang.SuppressWarnings("all")
        private String sedType;
        @java.lang.SuppressWarnings("all")
        private String status;
        @java.lang.SuppressWarnings("all")
        private String rinaUrl;

        @java.lang.SuppressWarnings("all")
        SedinfoDtoBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public SedinfoDto.SedinfoDtoBuilder bucId(final String bucId) {
            this.bucId = bucId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public SedinfoDto.SedinfoDtoBuilder sedId(final String sedId) {
            this.sedId = sedId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public SedinfoDto.SedinfoDtoBuilder opprettetDato(final Long opprettetDato) {
            this.opprettetDato = opprettetDato;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public SedinfoDto.SedinfoDtoBuilder sistOppdatert(final Long sistOppdatert) {
            this.sistOppdatert = sistOppdatert;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public SedinfoDto.SedinfoDtoBuilder sedType(final String sedType) {
            this.sedType = sedType;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public SedinfoDto.SedinfoDtoBuilder status(final String status) {
            this.status = status;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public SedinfoDto.SedinfoDtoBuilder rinaUrl(final String rinaUrl) {
            this.rinaUrl = rinaUrl;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public SedinfoDto build() {
            return new SedinfoDto(this.bucId, this.sedId, this.opprettetDato, this.sistOppdatert, this.sedType, this.status, this.rinaUrl);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "SedinfoDto.SedinfoDtoBuilder(bucId=" + this.bucId + ", sedId=" + this.sedId + ", opprettetDato=" + this.opprettetDato + ", sistOppdatert=" + this.sistOppdatert + ", sedType=" + this.sedType + ", status=" + this.status + ", rinaUrl=" + this.rinaUrl + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static SedinfoDto.SedinfoDtoBuilder builder() {
        return new SedinfoDto.SedinfoDtoBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public String getBucId() {
        return this.bucId;
    }

    @java.lang.SuppressWarnings("all")
    public String getSedId() {
        return this.sedId;
    }

    @java.lang.SuppressWarnings("all")
    public Long getOpprettetDato() {
        return this.opprettetDato;
    }

    @java.lang.SuppressWarnings("all")
    public Long getSistOppdatert() {
        return this.sistOppdatert;
    }

    @java.lang.SuppressWarnings("all")
    public String getSedType() {
        return this.sedType;
    }

    @java.lang.SuppressWarnings("all")
    public String getStatus() {
        return this.status;
    }

    @java.lang.SuppressWarnings("all")
    public String getRinaUrl() {
        return this.rinaUrl;
    }

    @java.lang.SuppressWarnings("all")
    public void setBucId(final String bucId) {
        this.bucId = bucId;
    }

    @java.lang.SuppressWarnings("all")
    public void setSedId(final String sedId) {
        this.sedId = sedId;
    }

    @java.lang.SuppressWarnings("all")
    public void setOpprettetDato(final Long opprettetDato) {
        this.opprettetDato = opprettetDato;
    }

    @java.lang.SuppressWarnings("all")
    public void setSistOppdatert(final Long sistOppdatert) {
        this.sistOppdatert = sistOppdatert;
    }

    @java.lang.SuppressWarnings("all")
    public void setSedType(final String sedType) {
        this.sedType = sedType;
    }

    @java.lang.SuppressWarnings("all")
    public void setStatus(final String status) {
        this.status = status;
    }

    @java.lang.SuppressWarnings("all")
    public void setRinaUrl(final String rinaUrl) {
        this.rinaUrl = rinaUrl;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof SedinfoDto)) return false;
        final SedinfoDto other = (SedinfoDto) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$opprettetDato = this.getOpprettetDato();
        final java.lang.Object other$opprettetDato = other.getOpprettetDato();
        if (this$opprettetDato == null ? other$opprettetDato != null : !this$opprettetDato.equals(other$opprettetDato)) return false;
        final java.lang.Object this$sistOppdatert = this.getSistOppdatert();
        final java.lang.Object other$sistOppdatert = other.getSistOppdatert();
        if (this$sistOppdatert == null ? other$sistOppdatert != null : !this$sistOppdatert.equals(other$sistOppdatert)) return false;
        final java.lang.Object this$bucId = this.getBucId();
        final java.lang.Object other$bucId = other.getBucId();
        if (this$bucId == null ? other$bucId != null : !this$bucId.equals(other$bucId)) return false;
        final java.lang.Object this$sedId = this.getSedId();
        final java.lang.Object other$sedId = other.getSedId();
        if (this$sedId == null ? other$sedId != null : !this$sedId.equals(other$sedId)) return false;
        final java.lang.Object this$sedType = this.getSedType();
        final java.lang.Object other$sedType = other.getSedType();
        if (this$sedType == null ? other$sedType != null : !this$sedType.equals(other$sedType)) return false;
        final java.lang.Object this$status = this.getStatus();
        final java.lang.Object other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) return false;
        final java.lang.Object this$rinaUrl = this.getRinaUrl();
        final java.lang.Object other$rinaUrl = other.getRinaUrl();
        if (this$rinaUrl == null ? other$rinaUrl != null : !this$rinaUrl.equals(other$rinaUrl)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof SedinfoDto;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $opprettetDato = this.getOpprettetDato();
        result = result * PRIME + ($opprettetDato == null ? 43 : $opprettetDato.hashCode());
        final java.lang.Object $sistOppdatert = this.getSistOppdatert();
        result = result * PRIME + ($sistOppdatert == null ? 43 : $sistOppdatert.hashCode());
        final java.lang.Object $bucId = this.getBucId();
        result = result * PRIME + ($bucId == null ? 43 : $bucId.hashCode());
        final java.lang.Object $sedId = this.getSedId();
        result = result * PRIME + ($sedId == null ? 43 : $sedId.hashCode());
        final java.lang.Object $sedType = this.getSedType();
        result = result * PRIME + ($sedType == null ? 43 : $sedType.hashCode());
        final java.lang.Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 43 : $status.hashCode());
        final java.lang.Object $rinaUrl = this.getRinaUrl();
        result = result * PRIME + ($rinaUrl == null ? 43 : $rinaUrl.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "SedinfoDto(bucId=" + this.getBucId() + ", sedId=" + this.getSedId() + ", opprettetDato=" + this.getOpprettetDato() + ", sistOppdatert=" + this.getSistOppdatert() + ", sedType=" + this.getSedType() + ", status=" + this.getStatus() + ", rinaUrl=" + this.getRinaUrl() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public SedinfoDto() {
    }

    @java.lang.SuppressWarnings("all")
    public SedinfoDto(final String bucId, final String sedId, final Long opprettetDato, final Long sistOppdatert, final String sedType, final String status, final String rinaUrl) {
        this.bucId = bucId;
        this.sedId = sedId;
        this.opprettetDato = opprettetDato;
        this.sistOppdatert = sistOppdatert;
        this.sedType = sedType;
        this.status = status;
        this.rinaUrl = rinaUrl;
    }
}
