// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity(name = "sed_mottatt_hendelse")
@Convert(attributeName = "jsonb", converter = JsonBinaryType.class)
@EntityListeners(AuditingEntityListener.class)
public class SedMottattHendelse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sed_hendelse")
    private SedHendelse sedHendelse;
    @Column(name = "journalpost_id")
    private String journalpostId;
    @Column(name = "publisert_kafka")
    private boolean publisertKafka;
    @CreatedDate
    @Column(name = "mottatt_dato")
    private LocalDateTime mottattDato;
    @LastModifiedDate
    @Column(name = "endret_dato")
    private LocalDateTime sistEndretDato;


    @java.lang.SuppressWarnings("all")
    public static class SedMottattHendelseBuilder {
        @java.lang.SuppressWarnings("all")
        private Long id;
        @java.lang.SuppressWarnings("all")
        private SedHendelse sedHendelse;
        @java.lang.SuppressWarnings("all")
        private String journalpostId;
        @java.lang.SuppressWarnings("all")
        private boolean publisertKafka;
        @java.lang.SuppressWarnings("all")
        private LocalDateTime mottattDato;
        @java.lang.SuppressWarnings("all")
        private LocalDateTime sistEndretDato;

        @java.lang.SuppressWarnings("all")
        SedMottattHendelseBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public SedMottattHendelse.SedMottattHendelseBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public SedMottattHendelse.SedMottattHendelseBuilder sedHendelse(final SedHendelse sedHendelse) {
            this.sedHendelse = sedHendelse;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public SedMottattHendelse.SedMottattHendelseBuilder journalpostId(final String journalpostId) {
            this.journalpostId = journalpostId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public SedMottattHendelse.SedMottattHendelseBuilder publisertKafka(final boolean publisertKafka) {
            this.publisertKafka = publisertKafka;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public SedMottattHendelse.SedMottattHendelseBuilder mottattDato(final LocalDateTime mottattDato) {
            this.mottattDato = mottattDato;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public SedMottattHendelse.SedMottattHendelseBuilder sistEndretDato(final LocalDateTime sistEndretDato) {
            this.sistEndretDato = sistEndretDato;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public SedMottattHendelse build() {
            return new SedMottattHendelse(this.id, this.sedHendelse, this.journalpostId, this.publisertKafka, this.mottattDato, this.sistEndretDato);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "SedMottattHendelse.SedMottattHendelseBuilder(id=" + this.id + ", sedHendelse=" + this.sedHendelse + ", journalpostId=" + this.journalpostId + ", publisertKafka=" + this.publisertKafka + ", mottattDato=" + this.mottattDato + ", sistEndretDato=" + this.sistEndretDato + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static SedMottattHendelse.SedMottattHendelseBuilder builder() {
        return new SedMottattHendelse.SedMottattHendelseBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public Long getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public SedHendelse getSedHendelse() {
        return this.sedHendelse;
    }

    @java.lang.SuppressWarnings("all")
    public String getJournalpostId() {
        return this.journalpostId;
    }

    @java.lang.SuppressWarnings("all")
    public boolean isPublisertKafka() {
        return this.publisertKafka;
    }

    @java.lang.SuppressWarnings("all")
    public LocalDateTime getMottattDato() {
        return this.mottattDato;
    }

    @java.lang.SuppressWarnings("all")
    public LocalDateTime getSistEndretDato() {
        return this.sistEndretDato;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final Long id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setSedHendelse(final SedHendelse sedHendelse) {
        this.sedHendelse = sedHendelse;
    }

    @java.lang.SuppressWarnings("all")
    public void setJournalpostId(final String journalpostId) {
        this.journalpostId = journalpostId;
    }

    @java.lang.SuppressWarnings("all")
    public void setPublisertKafka(final boolean publisertKafka) {
        this.publisertKafka = publisertKafka;
    }

    @java.lang.SuppressWarnings("all")
    public void setMottattDato(final LocalDateTime mottattDato) {
        this.mottattDato = mottattDato;
    }

    @java.lang.SuppressWarnings("all")
    public void setSistEndretDato(final LocalDateTime sistEndretDato) {
        this.sistEndretDato = sistEndretDato;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "SedMottattHendelse(id=" + this.getId() + ", sedHendelse=" + this.getSedHendelse() + ", journalpostId=" + this.getJournalpostId() + ", publisertKafka=" + this.isPublisertKafka() + ", mottattDato=" + this.getMottattDato() + ", sistEndretDato=" + this.getSistEndretDato() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public SedMottattHendelse() {
    }

    @java.lang.SuppressWarnings("all")
    public SedMottattHendelse(final Long id, final SedHendelse sedHendelse, final String journalpostId, final boolean publisertKafka, final LocalDateTime mottattDato, final LocalDateTime sistEndretDato) {
        this.id = id;
        this.sedHendelse = sedHendelse;
        this.journalpostId = journalpostId;
        this.publisertKafka = publisertKafka;
        this.mottattDato = mottattDato;
        this.sistEndretDato = sistEndretDato;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof SedMottattHendelse)) return false;
        final SedMottattHendelse other = (SedMottattHendelse) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (this.isPublisertKafka() != other.isPublisertKafka()) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$sedHendelse = this.getSedHendelse();
        final java.lang.Object other$sedHendelse = other.getSedHendelse();
        if (this$sedHendelse == null ? other$sedHendelse != null : !this$sedHendelse.equals(other$sedHendelse)) return false;
        final java.lang.Object this$journalpostId = this.getJournalpostId();
        final java.lang.Object other$journalpostId = other.getJournalpostId();
        if (this$journalpostId == null ? other$journalpostId != null : !this$journalpostId.equals(other$journalpostId)) return false;
        final java.lang.Object this$mottattDato = this.getMottattDato();
        final java.lang.Object other$mottattDato = other.getMottattDato();
        if (this$mottattDato == null ? other$mottattDato != null : !this$mottattDato.equals(other$mottattDato)) return false;
        final java.lang.Object this$sistEndretDato = this.getSistEndretDato();
        final java.lang.Object other$sistEndretDato = other.getSistEndretDato();
        if (this$sistEndretDato == null ? other$sistEndretDato != null : !this$sistEndretDato.equals(other$sistEndretDato)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof SedMottattHendelse;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isPublisertKafka() ? 79 : 97);
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $sedHendelse = this.getSedHendelse();
        result = result * PRIME + ($sedHendelse == null ? 43 : $sedHendelse.hashCode());
        final java.lang.Object $journalpostId = this.getJournalpostId();
        result = result * PRIME + ($journalpostId == null ? 43 : $journalpostId.hashCode());
        final java.lang.Object $mottattDato = this.getMottattDato();
        result = result * PRIME + ($mottattDato == null ? 43 : $mottattDato.hashCode());
        final java.lang.Object $sistEndretDato = this.getSistEndretDato();
        result = result * PRIME + ($sistEndretDato == null ? 43 : $sistEndretDato.hashCode());
        return result;
    }
}
