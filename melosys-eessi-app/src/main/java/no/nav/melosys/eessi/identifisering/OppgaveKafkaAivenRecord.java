package no.nav.melosys.eessi.identifisering;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record OppgaveKafkaAivenRecord(Hendelse hendelse, UtfortAv utfortAv, Oppgave oppgave) {
    public record Hendelse(Hendelsestype hendelsestype, LocalDateTime tidspunkt) {
        public enum Hendelsestype {OPPGAVE_OPPRETTET, OPPGAVE_ENDRET, OPPGAVE_FERDIGSTILT, OPPGAVE_FEILREGISTRERT}
    }

    public record UtfortAv(String navIdent, @Nullable String enhetsnr) {
    }

    public record Oppgave(Long oppgaveId, Integer versjon, Tilordning tilordning, Kategorisering kategorisering,
                          Behandlingsperiode behandlingsperiode, @Nullable Bruker bruker) {
    }

    public record Tilordning(String enhetsnr, @Nullable Long enhetsmappeId, @Nullable String navIdent) {
    }

    public record Kategorisering(String tema, String oppgavetype, @Nullable String behandlingstema,
                                 @Nullable String behandlingstype,
                                 Prioritet prioritet) {
        public enum Prioritet {HOY, NORMAL, LAV}
    }

    public record Behandlingsperiode(LocalDate aktiv, @Nullable LocalDate frist) {
    }

    public record Bruker(String ident, IdentType identType) {
        public enum IdentType {FOLKEREGISTERIDENT, NPID, ORGNR, SAMHANDLERNR}
    }

    @JsonIgnore
    public boolean harFolkeregisterIdent() {
        return oppgave() != null
            && oppgave().bruker != null
            && oppgave().bruker.identType() == Bruker.IdentType.FOLKEREGISTERIDENT
            && oppgave().bruker.ident != null;
    }

    @JsonIgnore
    public String hentFolkeregisterIdent() {
        return Optional.ofNullable(oppgave.bruker != null && oppgave.bruker.identType == Bruker.IdentType.FOLKEREGISTERIDENT ? oppgave.bruker.ident : null)
            .orElseThrow(() -> new NoSuchElementException("Finner ikke folkeregisterident"));
    }

    @JsonIgnore
    public boolean harSammeVersjon(int versjon) {
        return this.oppgave.versjon == versjon;
    }
}
