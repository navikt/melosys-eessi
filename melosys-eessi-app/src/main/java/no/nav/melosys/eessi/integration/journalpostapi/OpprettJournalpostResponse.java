// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.journalpostapi;

import java.util.List;

public class OpprettJournalpostResponse {
    private String journalpostId;
    private List<Dokument> dokumenter;
    private String journalstatus;
    private String melding;


    public static class Dokument {
        private String dokumentInfoId;

        @java.lang.SuppressWarnings("all")
        public String getDokumentInfoId() {
            return this.dokumentInfoId;
        }

        @java.lang.SuppressWarnings("all")
        public void setDokumentInfoId(final String dokumentInfoId) {
            this.dokumentInfoId = dokumentInfoId;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof OpprettJournalpostResponse.Dokument)) return false;
            final OpprettJournalpostResponse.Dokument other = (OpprettJournalpostResponse.Dokument) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$dokumentInfoId = this.getDokumentInfoId();
            final java.lang.Object other$dokumentInfoId = other.getDokumentInfoId();
            if (this$dokumentInfoId == null ? other$dokumentInfoId != null : !this$dokumentInfoId.equals(other$dokumentInfoId))
                return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof OpprettJournalpostResponse.Dokument;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $dokumentInfoId = this.getDokumentInfoId();
            result = result * PRIME + ($dokumentInfoId == null ? 43 : $dokumentInfoId.hashCode());
            return result;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "OpprettJournalpostResponse.Dokument(dokumentInfoId=" + this.getDokumentInfoId() + ")";
        }

        @java.lang.SuppressWarnings("all")
        public Dokument() {
        }

        @java.lang.SuppressWarnings("all")
        public Dokument(final String dokumentInfoId) {
            this.dokumentInfoId = dokumentInfoId;
        }
    }


    @java.lang.SuppressWarnings("all")
    public static class OpprettJournalpostResponseBuilder {
        @java.lang.SuppressWarnings("all")
        private String journalpostId;
        @java.lang.SuppressWarnings("all")
        private List<Dokument> dokumenter;
        @java.lang.SuppressWarnings("all")
        private String journalstatus;
        @java.lang.SuppressWarnings("all")
        private String melding;

        @java.lang.SuppressWarnings("all")
        OpprettJournalpostResponseBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OpprettJournalpostResponse.OpprettJournalpostResponseBuilder journalpostId(final String journalpostId) {
            this.journalpostId = journalpostId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OpprettJournalpostResponse.OpprettJournalpostResponseBuilder dokumenter(final List<Dokument> dokumenter) {
            this.dokumenter = dokumenter;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OpprettJournalpostResponse.OpprettJournalpostResponseBuilder journalstatus(final String journalstatus) {
            this.journalstatus = journalstatus;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OpprettJournalpostResponse.OpprettJournalpostResponseBuilder melding(final String melding) {
            this.melding = melding;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public OpprettJournalpostResponse build() {
            return new OpprettJournalpostResponse(this.journalpostId, this.dokumenter, this.journalstatus, this.melding);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "OpprettJournalpostResponse.OpprettJournalpostResponseBuilder(journalpostId=" + this.journalpostId + ", dokumenter=" + this.dokumenter + ", journalstatus=" + this.journalstatus + ", melding=" + this.melding + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static OpprettJournalpostResponse.OpprettJournalpostResponseBuilder builder() {
        return new OpprettJournalpostResponse.OpprettJournalpostResponseBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public String getJournalpostId() {
        return this.journalpostId;
    }

    @java.lang.SuppressWarnings("all")
    public List<Dokument> getDokumenter() {
        return this.dokumenter;
    }

    @java.lang.SuppressWarnings("all")
    public String getJournalstatus() {
        return this.journalstatus;
    }

    @java.lang.SuppressWarnings("all")
    public String getMelding() {
        return this.melding;
    }

    @java.lang.SuppressWarnings("all")
    public OpprettJournalpostResponse() {
    }

    @java.lang.SuppressWarnings("all")
    public OpprettJournalpostResponse(final String journalpostId, final List<Dokument> dokumenter, final String journalstatus, final String melding) {
        this.journalpostId = journalpostId;
        this.dokumenter = dokumenter;
        this.journalstatus = journalstatus;
        this.melding = melding;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "OpprettJournalpostResponse(journalpostId=" + this.getJournalpostId() + ", dokumenter=" + this.getDokumenter() + ", journalstatus=" + this.getJournalstatus() + ", melding=" + this.getMelding() + ")";
    }
}
