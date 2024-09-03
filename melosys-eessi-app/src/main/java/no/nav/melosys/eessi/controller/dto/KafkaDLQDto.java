// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.controller.dto;

import java.time.LocalDateTime;

import no.nav.melosys.eessi.models.kafkadlq.QueueType;

public class KafkaDLQDto {
    private String id;
    private QueueType queueType;
    private LocalDateTime tidRegistrert;
    private LocalDateTime tidSistRekjort;
    private String sisteFeilmelding;
    private int antallRekjoringer;
    private String melding;
    private Boolean skip;


    @java.lang.SuppressWarnings("all")
    public static class KafkaDLQDtoBuilder {
        @java.lang.SuppressWarnings("all")
        private String id;
        @java.lang.SuppressWarnings("all")
        private QueueType queueType;
        @java.lang.SuppressWarnings("all")
        private LocalDateTime tidRegistrert;
        @java.lang.SuppressWarnings("all")
        private LocalDateTime tidSistRekjort;
        @java.lang.SuppressWarnings("all")
        private String sisteFeilmelding;
        @java.lang.SuppressWarnings("all")
        private int antallRekjoringer;
        @java.lang.SuppressWarnings("all")
        private String melding;
        @java.lang.SuppressWarnings("all")
        private Boolean skip;

        @java.lang.SuppressWarnings("all")
        KafkaDLQDtoBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public KafkaDLQDto.KafkaDLQDtoBuilder id(final String id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public KafkaDLQDto.KafkaDLQDtoBuilder queueType(final QueueType queueType) {
            this.queueType = queueType;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public KafkaDLQDto.KafkaDLQDtoBuilder tidRegistrert(final LocalDateTime tidRegistrert) {
            this.tidRegistrert = tidRegistrert;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public KafkaDLQDto.KafkaDLQDtoBuilder tidSistRekjort(final LocalDateTime tidSistRekjort) {
            this.tidSistRekjort = tidSistRekjort;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public KafkaDLQDto.KafkaDLQDtoBuilder sisteFeilmelding(final String sisteFeilmelding) {
            this.sisteFeilmelding = sisteFeilmelding;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public KafkaDLQDto.KafkaDLQDtoBuilder antallRekjoringer(final int antallRekjoringer) {
            this.antallRekjoringer = antallRekjoringer;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public KafkaDLQDto.KafkaDLQDtoBuilder melding(final String melding) {
            this.melding = melding;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public KafkaDLQDto.KafkaDLQDtoBuilder skip(final Boolean skip) {
            this.skip = skip;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public KafkaDLQDto build() {
            return new KafkaDLQDto(this.id, this.queueType, this.tidRegistrert, this.tidSistRekjort, this.sisteFeilmelding, this.antallRekjoringer, this.melding, this.skip);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "KafkaDLQDto.KafkaDLQDtoBuilder(id=" + this.id + ", queueType=" + this.queueType + ", tidRegistrert=" + this.tidRegistrert + ", tidSistRekjort=" + this.tidSistRekjort + ", sisteFeilmelding=" + this.sisteFeilmelding + ", antallRekjoringer=" + this.antallRekjoringer + ", melding=" + this.melding + ", skip=" + this.skip + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static KafkaDLQDto.KafkaDLQDtoBuilder builder() {
        return new KafkaDLQDto.KafkaDLQDtoBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public KafkaDLQDto() {
    }

    @java.lang.SuppressWarnings("all")
    public KafkaDLQDto(final String id, final QueueType queueType, final LocalDateTime tidRegistrert, final LocalDateTime tidSistRekjort, final String sisteFeilmelding, final int antallRekjoringer, final String melding, final Boolean skip) {
        this.id = id;
        this.queueType = queueType;
        this.tidRegistrert = tidRegistrert;
        this.tidSistRekjort = tidSistRekjort;
        this.sisteFeilmelding = sisteFeilmelding;
        this.antallRekjoringer = antallRekjoringer;
        this.melding = melding;
        this.skip = skip;
    }

    @java.lang.SuppressWarnings("all")
    public String getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public QueueType getQueueType() {
        return this.queueType;
    }

    @java.lang.SuppressWarnings("all")
    public LocalDateTime getTidRegistrert() {
        return this.tidRegistrert;
    }

    @java.lang.SuppressWarnings("all")
    public LocalDateTime getTidSistRekjort() {
        return this.tidSistRekjort;
    }

    @java.lang.SuppressWarnings("all")
    public String getSisteFeilmelding() {
        return this.sisteFeilmelding;
    }

    @java.lang.SuppressWarnings("all")
    public int getAntallRekjoringer() {
        return this.antallRekjoringer;
    }

    @java.lang.SuppressWarnings("all")
    public String getMelding() {
        return this.melding;
    }

    @java.lang.SuppressWarnings("all")
    public Boolean getSkip() {
        return this.skip;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final String id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setQueueType(final QueueType queueType) {
        this.queueType = queueType;
    }

    @java.lang.SuppressWarnings("all")
    public void setTidRegistrert(final LocalDateTime tidRegistrert) {
        this.tidRegistrert = tidRegistrert;
    }

    @java.lang.SuppressWarnings("all")
    public void setTidSistRekjort(final LocalDateTime tidSistRekjort) {
        this.tidSistRekjort = tidSistRekjort;
    }

    @java.lang.SuppressWarnings("all")
    public void setSisteFeilmelding(final String sisteFeilmelding) {
        this.sisteFeilmelding = sisteFeilmelding;
    }

    @java.lang.SuppressWarnings("all")
    public void setAntallRekjoringer(final int antallRekjoringer) {
        this.antallRekjoringer = antallRekjoringer;
    }

    @java.lang.SuppressWarnings("all")
    public void setMelding(final String melding) {
        this.melding = melding;
    }

    @java.lang.SuppressWarnings("all")
    public void setSkip(final Boolean skip) {
        this.skip = skip;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof KafkaDLQDto)) return false;
        final KafkaDLQDto other = (KafkaDLQDto) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (this.getAntallRekjoringer() != other.getAntallRekjoringer()) return false;
        final java.lang.Object this$skip = this.getSkip();
        final java.lang.Object other$skip = other.getSkip();
        if (this$skip == null ? other$skip != null : !this$skip.equals(other$skip)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$queueType = this.getQueueType();
        final java.lang.Object other$queueType = other.getQueueType();
        if (this$queueType == null ? other$queueType != null : !this$queueType.equals(other$queueType)) return false;
        final java.lang.Object this$tidRegistrert = this.getTidRegistrert();
        final java.lang.Object other$tidRegistrert = other.getTidRegistrert();
        if (this$tidRegistrert == null ? other$tidRegistrert != null : !this$tidRegistrert.equals(other$tidRegistrert))
            return false;
        final java.lang.Object this$tidSistRekjort = this.getTidSistRekjort();
        final java.lang.Object other$tidSistRekjort = other.getTidSistRekjort();
        if (this$tidSistRekjort == null ? other$tidSistRekjort != null : !this$tidSistRekjort.equals(other$tidSistRekjort))
            return false;
        final java.lang.Object this$sisteFeilmelding = this.getSisteFeilmelding();
        final java.lang.Object other$sisteFeilmelding = other.getSisteFeilmelding();
        if (this$sisteFeilmelding == null ? other$sisteFeilmelding != null : !this$sisteFeilmelding.equals(other$sisteFeilmelding))
            return false;
        final java.lang.Object this$melding = this.getMelding();
        final java.lang.Object other$melding = other.getMelding();
        if (this$melding == null ? other$melding != null : !this$melding.equals(other$melding)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof KafkaDLQDto;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getAntallRekjoringer();
        final java.lang.Object $skip = this.getSkip();
        result = result * PRIME + ($skip == null ? 43 : $skip.hashCode());
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $queueType = this.getQueueType();
        result = result * PRIME + ($queueType == null ? 43 : $queueType.hashCode());
        final java.lang.Object $tidRegistrert = this.getTidRegistrert();
        result = result * PRIME + ($tidRegistrert == null ? 43 : $tidRegistrert.hashCode());
        final java.lang.Object $tidSistRekjort = this.getTidSistRekjort();
        result = result * PRIME + ($tidSistRekjort == null ? 43 : $tidSistRekjort.hashCode());
        final java.lang.Object $sisteFeilmelding = this.getSisteFeilmelding();
        result = result * PRIME + ($sisteFeilmelding == null ? 43 : $sisteFeilmelding.hashCode());
        final java.lang.Object $melding = this.getMelding();
        result = result * PRIME + ($melding == null ? 43 : $melding.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "KafkaDLQDto(id=" + this.getId() + ", queueType=" + this.getQueueType() + ", tidRegistrert=" + this.getTidRegistrert() + ", tidSistRekjort=" + this.getTidSistRekjort() + ", sisteFeilmelding=" + this.getSisteFeilmelding() + ", antallRekjoringer=" + this.getAntallRekjoringer() + ", melding=" + this.getMelding() + ", skip=" + this.getSkip() + ")";
    }
}
