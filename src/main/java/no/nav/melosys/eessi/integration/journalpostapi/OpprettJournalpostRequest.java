package no.nav.melosys.eessi.integration.journalpostapi;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OpprettJournalpostRequest {

  @NonNull
  private JournalpostType journalpostType;
  private AvsenderMottaker avsenderMottaker;
  private Bruker bruker;
  @NonNull
  private String tema;
  private String behandlingstema;
  @NonNull
  private String tittel;
  private String kanal;
  //"Ved automatisk journalføring uten mennesker involvert skal enhet settes til \"9999\"."
  private String journalfoerendeEnhet;
  private String eksternReferanseId;

  @Builder.Default
  private List<Tilleggsopplysning> tilleggsopplysninger = new ArrayList<>();

  private Sak sak;

  //"Første dokument blir tilknyttet som hoveddokument på journalposten. Øvrige dokumenter tilknyttes som vedlegg. Rekkefølgen på vedlegg beholdes ikke ved uthenting av journalpost."
  private List<Dokument> dokumenter;

  public enum JournalpostType {
    INNGAAENDE,
    UTGAAENDE,
    NOTAT
  }

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AvsenderMottaker {

    private String id;
    private String navn;
    private String land;
    private IdType idType;

    public enum IdType {
      FNR, ORGNR, HPRNR, UTL_ORG
    }
  }

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Bruker {

    @NonNull
    private BrukerIdType idType;
    @NonNull
    private String id;
  }

  public enum BrukerIdType {
    FNR,
    ORGNR
  }

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Tilleggsopplysning {

    private String nokkel;
    private String verdi;
  }

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Sak {

    @NonNull
    private String arkivsaksnummer;
    private final String arkivsaksystem = "GSAK";
  }

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Dokument {

    private String tittel;
    @JsonProperty("brevkode")
    private String sedType;
    private final String dokumentKategori = "SED";
    private List<DokumentVariant> dokumentvarianter;
  }

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DokumentVariant {

    @NonNull
    private JournalpostFiltype filtype = JournalpostFiltype.PDFA;

    /**
     * "ARKIV brukes for dokumentvarianter i menneskelesbart format (for eksempel PDF/A).  Gosys og
     * nav.no henter arkivvariant og viser denne til bruker.\n" + "ORIGINAL skal brukes for
     * dokumentvariant i maskinlesbart format (for eksempel XML og JSON) som brukes for automatisk
     * saksbehandling\n" + "Alle dokumenter må ha én variant med variantFormat ARKIV."
     */
    @NonNull
    private String variantformat;
    @NonNull
    private byte[] fysiskDokument;
  }

  public enum JournalpostFiltype {
    PDF,
    PDFA
  }
}
