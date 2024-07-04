// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.journalpostapi;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
  private List<Tilleggsopplysning> tilleggsopplysninger;
  private Sak sak;
  //"Første dokument blir tilknyttet som hoveddokument på journalposten. Øvrige dokumenter tilknyttes som vedlegg. Rekkefølgen på vedlegg beholdes ikke ved uthenting av journalpost."
  private List<Dokument> dokumenter;


  public enum JournalpostType {
    INNGAAENDE, UTGAAENDE, NOTAT;
  }


  public static class AvsenderMottaker {
    private String id;
    private String navn;
    private String land;
    private IdType idType;


    public enum IdType {
      FNR, ORGNR, HPRNR, UTL_ORG;
    }


    @java.lang.SuppressWarnings("all")
    public static class AvsenderMottakerBuilder {
      @java.lang.SuppressWarnings("all")
      private String id;
      @java.lang.SuppressWarnings("all")
      private String navn;
      @java.lang.SuppressWarnings("all")
      private String land;
      @java.lang.SuppressWarnings("all")
      private IdType idType;

      @java.lang.SuppressWarnings("all")
      AvsenderMottakerBuilder() {
      }

      /**
       * @return {@code this}.
       */
      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.AvsenderMottaker.AvsenderMottakerBuilder id(final String id) {
        this.id = id;
        return this;
      }

      /**
       * @return {@code this}.
       */
      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.AvsenderMottaker.AvsenderMottakerBuilder navn(final String navn) {
        this.navn = navn;
        return this;
      }

      /**
       * @return {@code this}.
       */
      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.AvsenderMottaker.AvsenderMottakerBuilder land(final String land) {
        this.land = land;
        return this;
      }

      /**
       * @return {@code this}.
       */
      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.AvsenderMottaker.AvsenderMottakerBuilder idType(final IdType idType) {
        this.idType = idType;
        return this;
      }

      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.AvsenderMottaker build() {
        return new OpprettJournalpostRequest.AvsenderMottaker(this.id, this.navn, this.land, this.idType);
      }

      @java.lang.Override
      @java.lang.SuppressWarnings("all")
      public java.lang.String toString() {
        return "OpprettJournalpostRequest.AvsenderMottaker.AvsenderMottakerBuilder(id=" + this.id + ", navn=" + this.navn + ", land=" + this.land + ", idType=" + this.idType + ")";
      }
    }

    @java.lang.SuppressWarnings("all")
    public static OpprettJournalpostRequest.AvsenderMottaker.AvsenderMottakerBuilder builder() {
      return new OpprettJournalpostRequest.AvsenderMottaker.AvsenderMottakerBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public String getId() {
      return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public String getNavn() {
      return this.navn;
    }

    @java.lang.SuppressWarnings("all")
    public String getLand() {
      return this.land;
    }

    @java.lang.SuppressWarnings("all")
    public IdType getIdType() {
      return this.idType;
    }

    @java.lang.SuppressWarnings("all")
    public AvsenderMottaker() {
    }

    @java.lang.SuppressWarnings("all")
    public AvsenderMottaker(final String id, final String navn, final String land, final IdType idType) {
      this.id = id;
      this.navn = navn;
      this.land = land;
      this.idType = idType;
    }
  }


  public static class Bruker {
    @NonNull
    private BrukerIdType idType;
    @NonNull
    private String id;


    @java.lang.SuppressWarnings("all")
    public static class BrukerBuilder {
      @java.lang.SuppressWarnings("all")
      private BrukerIdType idType;
      @java.lang.SuppressWarnings("all")
      private String id;

      @java.lang.SuppressWarnings("all")
      BrukerBuilder() {
      }

      /**
       * @return {@code this}.
       */
      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.Bruker.BrukerBuilder idType(@NonNull final BrukerIdType idType) {
        if (idType == null) {
          throw new java.lang.NullPointerException("idType is marked non-null but is null");
        }
        this.idType = idType;
        return this;
      }

      /**
       * @return {@code this}.
       */
      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.Bruker.BrukerBuilder id(@NonNull final String id) {
        if (id == null) {
          throw new java.lang.NullPointerException("id is marked non-null but is null");
        }
        this.id = id;
        return this;
      }

      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.Bruker build() {
        return new OpprettJournalpostRequest.Bruker(this.idType, this.id);
      }

      @java.lang.Override
      @java.lang.SuppressWarnings("all")
      public java.lang.String toString() {
        return "OpprettJournalpostRequest.Bruker.BrukerBuilder(idType=" + this.idType + ", id=" + this.id + ")";
      }
    }

    @java.lang.SuppressWarnings("all")
    public static OpprettJournalpostRequest.Bruker.BrukerBuilder builder() {
      return new OpprettJournalpostRequest.Bruker.BrukerBuilder();
    }

    @NonNull
    @java.lang.SuppressWarnings("all")
    public BrukerIdType getIdType() {
      return this.idType;
    }

    @NonNull
    @java.lang.SuppressWarnings("all")
    public String getId() {
      return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public Bruker() {
    }

    @java.lang.SuppressWarnings("all")
    public Bruker(@NonNull final BrukerIdType idType, @NonNull final String id) {
      if (idType == null) {
        throw new java.lang.NullPointerException("idType is marked non-null but is null");
      }
      if (id == null) {
        throw new java.lang.NullPointerException("id is marked non-null but is null");
      }
      this.idType = idType;
      this.id = id;
    }
  }


  public enum BrukerIdType {
    FNR, ORGNR;
  }


  public static class Tilleggsopplysning {
    private String nokkel;
    private String verdi;


    @java.lang.SuppressWarnings("all")
    public static class TilleggsopplysningBuilder {
      @java.lang.SuppressWarnings("all")
      private String nokkel;
      @java.lang.SuppressWarnings("all")
      private String verdi;

      @java.lang.SuppressWarnings("all")
      TilleggsopplysningBuilder() {
      }

      /**
       * @return {@code this}.
       */
      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.Tilleggsopplysning.TilleggsopplysningBuilder nokkel(final String nokkel) {
        this.nokkel = nokkel;
        return this;
      }

      /**
       * @return {@code this}.
       */
      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.Tilleggsopplysning.TilleggsopplysningBuilder verdi(final String verdi) {
        this.verdi = verdi;
        return this;
      }

      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.Tilleggsopplysning build() {
        return new OpprettJournalpostRequest.Tilleggsopplysning(this.nokkel, this.verdi);
      }

      @java.lang.Override
      @java.lang.SuppressWarnings("all")
      public java.lang.String toString() {
        return "OpprettJournalpostRequest.Tilleggsopplysning.TilleggsopplysningBuilder(nokkel=" + this.nokkel + ", verdi=" + this.verdi + ")";
      }
    }

    @java.lang.SuppressWarnings("all")
    public static OpprettJournalpostRequest.Tilleggsopplysning.TilleggsopplysningBuilder builder() {
      return new OpprettJournalpostRequest.Tilleggsopplysning.TilleggsopplysningBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public String getNokkel() {
      return this.nokkel;
    }

    @java.lang.SuppressWarnings("all")
    public String getVerdi() {
      return this.verdi;
    }

    @java.lang.SuppressWarnings("all")
    public Tilleggsopplysning() {
    }

    @java.lang.SuppressWarnings("all")
    public Tilleggsopplysning(final String nokkel, final String verdi) {
      this.nokkel = nokkel;
      this.verdi = verdi;
    }
  }


  public static class Sak {
    @NonNull
    private String arkivsaksnummer;
    private final String arkivsaksystem = "GSAK";


    @java.lang.SuppressWarnings("all")
    public static class SakBuilder {
      @java.lang.SuppressWarnings("all")
      private String arkivsaksnummer;

      @java.lang.SuppressWarnings("all")
      SakBuilder() {
      }

      /**
       * @return {@code this}.
       */
      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.Sak.SakBuilder arkivsaksnummer(@NonNull final String arkivsaksnummer) {
        if (arkivsaksnummer == null) {
          throw new java.lang.NullPointerException("arkivsaksnummer is marked non-null but is null");
        }
        this.arkivsaksnummer = arkivsaksnummer;
        return this;
      }

      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.Sak build() {
        return new OpprettJournalpostRequest.Sak(this.arkivsaksnummer);
      }

      @java.lang.Override
      @java.lang.SuppressWarnings("all")
      public java.lang.String toString() {
        return "OpprettJournalpostRequest.Sak.SakBuilder(arkivsaksnummer=" + this.arkivsaksnummer + ")";
      }
    }

    @java.lang.SuppressWarnings("all")
    public static OpprettJournalpostRequest.Sak.SakBuilder builder() {
      return new OpprettJournalpostRequest.Sak.SakBuilder();
    }

    @NonNull
    @java.lang.SuppressWarnings("all")
    public String getArkivsaksnummer() {
      return this.arkivsaksnummer;
    }

    @java.lang.SuppressWarnings("all")
    public String getArkivsaksystem() {
      return this.arkivsaksystem;
    }

    @java.lang.SuppressWarnings("all")
    public Sak() {
    }

    @java.lang.SuppressWarnings("all")
    public Sak(@NonNull final String arkivsaksnummer) {
      if (arkivsaksnummer == null) {
        throw new java.lang.NullPointerException("arkivsaksnummer is marked non-null but is null");
      }
      this.arkivsaksnummer = arkivsaksnummer;
    }
  }


  public static class Dokument {
    private String tittel;
    @JsonProperty("brevkode")
    private String sedType;
    private final String dokumentKategori = "SED";
    private List<DokumentVariant> dokumentvarianter;


    @java.lang.SuppressWarnings("all")
    public static class DokumentBuilder {
      @java.lang.SuppressWarnings("all")
      private String tittel;
      @java.lang.SuppressWarnings("all")
      private String sedType;
      @java.lang.SuppressWarnings("all")
      private List<DokumentVariant> dokumentvarianter;

      @java.lang.SuppressWarnings("all")
      DokumentBuilder() {
      }

      /**
       * @return {@code this}.
       */
      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.Dokument.DokumentBuilder tittel(final String tittel) {
        this.tittel = tittel;
        return this;
      }

      /**
       * @return {@code this}.
       */
      @JsonProperty("brevkode")
      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.Dokument.DokumentBuilder sedType(final String sedType) {
        this.sedType = sedType;
        return this;
      }

      /**
       * @return {@code this}.
       */
      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.Dokument.DokumentBuilder dokumentvarianter(final List<DokumentVariant> dokumentvarianter) {
        this.dokumentvarianter = dokumentvarianter;
        return this;
      }

      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.Dokument build() {
        return new OpprettJournalpostRequest.Dokument(this.tittel, this.sedType, this.dokumentvarianter);
      }

      @java.lang.Override
      @java.lang.SuppressWarnings("all")
      public java.lang.String toString() {
        return "OpprettJournalpostRequest.Dokument.DokumentBuilder(tittel=" + this.tittel + ", sedType=" + this.sedType + ", dokumentvarianter=" + this.dokumentvarianter + ")";
      }
    }

    @java.lang.SuppressWarnings("all")
    public static OpprettJournalpostRequest.Dokument.DokumentBuilder builder() {
      return new OpprettJournalpostRequest.Dokument.DokumentBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public String getTittel() {
      return this.tittel;
    }

    @java.lang.SuppressWarnings("all")
    public String getSedType() {
      return this.sedType;
    }

    @java.lang.SuppressWarnings("all")
    public String getDokumentKategori() {
      return this.dokumentKategori;
    }

    @java.lang.SuppressWarnings("all")
    public List<DokumentVariant> getDokumentvarianter() {
      return this.dokumentvarianter;
    }

    @java.lang.SuppressWarnings("all")
    public Dokument() {
    }

    @java.lang.SuppressWarnings("all")
    public Dokument(final String tittel, final String sedType, final List<DokumentVariant> dokumentvarianter) {
      this.tittel = tittel;
      this.sedType = sedType;
      this.dokumentvarianter = dokumentvarianter;
    }
  }


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


    @java.lang.SuppressWarnings("all")
    public static class DokumentVariantBuilder {
      @java.lang.SuppressWarnings("all")
      private JournalpostFiltype filtype;
      @java.lang.SuppressWarnings("all")
      private String variantformat;
      @java.lang.SuppressWarnings("all")
      private byte[] fysiskDokument;

      @java.lang.SuppressWarnings("all")
      DokumentVariantBuilder() {
      }

      /**
       * @return {@code this}.
       */
      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.DokumentVariant.DokumentVariantBuilder filtype(@NonNull final JournalpostFiltype filtype) {
        if (filtype == null) {
          throw new java.lang.NullPointerException("filtype is marked non-null but is null");
        }
        this.filtype = filtype;
        return this;
      }

      /**
       * @return {@code this}.
       */
      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.DokumentVariant.DokumentVariantBuilder variantformat(@NonNull final String variantformat) {
        if (variantformat == null) {
          throw new java.lang.NullPointerException("variantformat is marked non-null but is null");
        }
        this.variantformat = variantformat;
        return this;
      }

      /**
       * @return {@code this}.
       */
      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.DokumentVariant.DokumentVariantBuilder fysiskDokument(@NonNull final byte[] fysiskDokument) {
        if (fysiskDokument == null) {
          throw new java.lang.NullPointerException("fysiskDokument is marked non-null but is null");
        }
        this.fysiskDokument = fysiskDokument;
        return this;
      }

      @java.lang.SuppressWarnings("all")
      public OpprettJournalpostRequest.DokumentVariant build() {
        return new OpprettJournalpostRequest.DokumentVariant(this.filtype, this.variantformat, this.fysiskDokument);
      }

      @java.lang.Override
      @java.lang.SuppressWarnings("all")
      public java.lang.String toString() {
        return "OpprettJournalpostRequest.DokumentVariant.DokumentVariantBuilder(filtype=" + this.filtype + ", variantformat=" + this.variantformat + ", fysiskDokument=" + java.util.Arrays.toString(this.fysiskDokument) + ")";
      }
    }

    @java.lang.SuppressWarnings("all")
    public static OpprettJournalpostRequest.DokumentVariant.DokumentVariantBuilder builder() {
      return new OpprettJournalpostRequest.DokumentVariant.DokumentVariantBuilder();
    }

    @NonNull
    @java.lang.SuppressWarnings("all")
    public JournalpostFiltype getFiltype() {
      return this.filtype;
    }

    @NonNull
    @java.lang.SuppressWarnings("all")
    public String getVariantformat() {
      return this.variantformat;
    }

    @NonNull
    @java.lang.SuppressWarnings("all")
    public byte[] getFysiskDokument() {
      return this.fysiskDokument;
    }

    @java.lang.SuppressWarnings("all")
    public DokumentVariant() {
    }

    @java.lang.SuppressWarnings("all")
    public DokumentVariant(@NonNull final JournalpostFiltype filtype, @NonNull final String variantformat, @NonNull final byte[] fysiskDokument) {
      if (filtype == null) {
        throw new java.lang.NullPointerException("filtype is marked non-null but is null");
      }
      if (variantformat == null) {
        throw new java.lang.NullPointerException("variantformat is marked non-null but is null");
      }
      if (fysiskDokument == null) {
        throw new java.lang.NullPointerException("fysiskDokument is marked non-null but is null");
      }
      this.filtype = filtype;
      this.variantformat = variantformat;
      this.fysiskDokument = fysiskDokument;
    }
  }

  @java.lang.SuppressWarnings("all")
  private static List<Tilleggsopplysning> $default$tilleggsopplysninger() {
    return new ArrayList<>();
  }


  @java.lang.SuppressWarnings("all")
  public static class OpprettJournalpostRequestBuilder {
    @java.lang.SuppressWarnings("all")
    private JournalpostType journalpostType;
    @java.lang.SuppressWarnings("all")
    private AvsenderMottaker avsenderMottaker;
    @java.lang.SuppressWarnings("all")
    private Bruker bruker;
    @java.lang.SuppressWarnings("all")
    private String tema;
    @java.lang.SuppressWarnings("all")
    private String behandlingstema;
    @java.lang.SuppressWarnings("all")
    private String tittel;
    @java.lang.SuppressWarnings("all")
    private String kanal;
    @java.lang.SuppressWarnings("all")
    private String journalfoerendeEnhet;
    @java.lang.SuppressWarnings("all")
    private String eksternReferanseId;
    @java.lang.SuppressWarnings("all")
    private boolean tilleggsopplysninger$set;
    @java.lang.SuppressWarnings("all")
    private List<Tilleggsopplysning> tilleggsopplysninger$value;
    @java.lang.SuppressWarnings("all")
    private Sak sak;
    @java.lang.SuppressWarnings("all")
    private List<Dokument> dokumenter;

    @java.lang.SuppressWarnings("all")
    OpprettJournalpostRequestBuilder() {
    }

    /**
     * @return {@code this}.
     */
    @java.lang.SuppressWarnings("all")
    public OpprettJournalpostRequest.OpprettJournalpostRequestBuilder journalpostType(@NonNull final JournalpostType journalpostType) {
      if (journalpostType == null) {
        throw new java.lang.NullPointerException("journalpostType is marked non-null but is null");
      }
      this.journalpostType = journalpostType;
      return this;
    }

    /**
     * @return {@code this}.
     */
    @java.lang.SuppressWarnings("all")
    public OpprettJournalpostRequest.OpprettJournalpostRequestBuilder avsenderMottaker(final AvsenderMottaker avsenderMottaker) {
      this.avsenderMottaker = avsenderMottaker;
      return this;
    }

    /**
     * @return {@code this}.
     */
    @java.lang.SuppressWarnings("all")
    public OpprettJournalpostRequest.OpprettJournalpostRequestBuilder bruker(final Bruker bruker) {
      this.bruker = bruker;
      return this;
    }

    /**
     * @return {@code this}.
     */
    @java.lang.SuppressWarnings("all")
    public OpprettJournalpostRequest.OpprettJournalpostRequestBuilder tema(@NonNull final String tema) {
      if (tema == null) {
        throw new java.lang.NullPointerException("tema is marked non-null but is null");
      }
      this.tema = tema;
      return this;
    }

    /**
     * @return {@code this}.
     */
    @java.lang.SuppressWarnings("all")
    public OpprettJournalpostRequest.OpprettJournalpostRequestBuilder behandlingstema(final String behandlingstema) {
      this.behandlingstema = behandlingstema;
      return this;
    }

    /**
     * @return {@code this}.
     */
    @java.lang.SuppressWarnings("all")
    public OpprettJournalpostRequest.OpprettJournalpostRequestBuilder tittel(@NonNull final String tittel) {
      if (tittel == null) {
        throw new java.lang.NullPointerException("tittel is marked non-null but is null");
      }
      this.tittel = tittel;
      return this;
    }

    /**
     * @return {@code this}.
     */
    @java.lang.SuppressWarnings("all")
    public OpprettJournalpostRequest.OpprettJournalpostRequestBuilder kanal(final String kanal) {
      this.kanal = kanal;
      return this;
    }

    /**
     * @return {@code this}.
     */
    @java.lang.SuppressWarnings("all")
    public OpprettJournalpostRequest.OpprettJournalpostRequestBuilder journalfoerendeEnhet(final String journalfoerendeEnhet) {
      this.journalfoerendeEnhet = journalfoerendeEnhet;
      return this;
    }

    /**
     * @return {@code this}.
     */
    @java.lang.SuppressWarnings("all")
    public OpprettJournalpostRequest.OpprettJournalpostRequestBuilder eksternReferanseId(final String eksternReferanseId) {
      this.eksternReferanseId = eksternReferanseId;
      return this;
    }

    /**
     * @return {@code this}.
     */
    @java.lang.SuppressWarnings("all")
    public OpprettJournalpostRequest.OpprettJournalpostRequestBuilder tilleggsopplysninger(final List<Tilleggsopplysning> tilleggsopplysninger) {
      this.tilleggsopplysninger$value = tilleggsopplysninger;
      tilleggsopplysninger$set = true;
      return this;
    }

    /**
     * @return {@code this}.
     */
    @java.lang.SuppressWarnings("all")
    public OpprettJournalpostRequest.OpprettJournalpostRequestBuilder sak(final Sak sak) {
      this.sak = sak;
      return this;
    }

    /**
     * @return {@code this}.
     */
    @java.lang.SuppressWarnings("all")
    public OpprettJournalpostRequest.OpprettJournalpostRequestBuilder dokumenter(final List<Dokument> dokumenter) {
      this.dokumenter = dokumenter;
      return this;
    }

    @java.lang.SuppressWarnings("all")
    public OpprettJournalpostRequest build() {
      List<Tilleggsopplysning> tilleggsopplysninger$value = this.tilleggsopplysninger$value;
      if (!this.tilleggsopplysninger$set) tilleggsopplysninger$value = OpprettJournalpostRequest.$default$tilleggsopplysninger();
      return new OpprettJournalpostRequest(this.journalpostType, this.avsenderMottaker, this.bruker, this.tema, this.behandlingstema, this.tittel, this.kanal, this.journalfoerendeEnhet, this.eksternReferanseId, tilleggsopplysninger$value, this.sak, this.dokumenter);
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
      return "OpprettJournalpostRequest.OpprettJournalpostRequestBuilder(journalpostType=" + this.journalpostType + ", avsenderMottaker=" + this.avsenderMottaker + ", bruker=" + this.bruker + ", tema=" + this.tema + ", behandlingstema=" + this.behandlingstema + ", tittel=" + this.tittel + ", kanal=" + this.kanal + ", journalfoerendeEnhet=" + this.journalfoerendeEnhet + ", eksternReferanseId=" + this.eksternReferanseId + ", tilleggsopplysninger$value=" + this.tilleggsopplysninger$value + ", sak=" + this.sak + ", dokumenter=" + this.dokumenter + ")";
    }
  }

  @java.lang.SuppressWarnings("all")
  public static OpprettJournalpostRequest.OpprettJournalpostRequestBuilder builder() {
    return new OpprettJournalpostRequest.OpprettJournalpostRequestBuilder();
  }

  @NonNull
  @java.lang.SuppressWarnings("all")
  public JournalpostType getJournalpostType() {
    return this.journalpostType;
  }

  @java.lang.SuppressWarnings("all")
  public AvsenderMottaker getAvsenderMottaker() {
    return this.avsenderMottaker;
  }

  @java.lang.SuppressWarnings("all")
  public Bruker getBruker() {
    return this.bruker;
  }

  @NonNull
  @java.lang.SuppressWarnings("all")
  public String getTema() {
    return this.tema;
  }

  @java.lang.SuppressWarnings("all")
  public String getBehandlingstema() {
    return this.behandlingstema;
  }

  @NonNull
  @java.lang.SuppressWarnings("all")
  public String getTittel() {
    return this.tittel;
  }

  @java.lang.SuppressWarnings("all")
  public String getKanal() {
    return this.kanal;
  }

  @java.lang.SuppressWarnings("all")
  public String getJournalfoerendeEnhet() {
    return this.journalfoerendeEnhet;
  }

  @java.lang.SuppressWarnings("all")
  public String getEksternReferanseId() {
    return this.eksternReferanseId;
  }

  @java.lang.SuppressWarnings("all")
  public List<Tilleggsopplysning> getTilleggsopplysninger() {
    return this.tilleggsopplysninger;
  }

  @java.lang.SuppressWarnings("all")
  public Sak getSak() {
    return this.sak;
  }

  @java.lang.SuppressWarnings("all")
  public List<Dokument> getDokumenter() {
    return this.dokumenter;
  }

  @java.lang.SuppressWarnings("all")
  public OpprettJournalpostRequest() {
    this.tilleggsopplysninger = OpprettJournalpostRequest.$default$tilleggsopplysninger();
  }

  @java.lang.SuppressWarnings("all")
  public OpprettJournalpostRequest(@NonNull final JournalpostType journalpostType, final AvsenderMottaker avsenderMottaker, final Bruker bruker, @NonNull final String tema, final String behandlingstema, @NonNull final String tittel, final String kanal, final String journalfoerendeEnhet, final String eksternReferanseId, final List<Tilleggsopplysning> tilleggsopplysninger, final Sak sak, final List<Dokument> dokumenter) {
    if (journalpostType == null) {
      throw new java.lang.NullPointerException("journalpostType is marked non-null but is null");
    }
    if (tema == null) {
      throw new java.lang.NullPointerException("tema is marked non-null but is null");
    }
    if (tittel == null) {
      throw new java.lang.NullPointerException("tittel is marked non-null but is null");
    }
    this.journalpostType = journalpostType;
    this.avsenderMottaker = avsenderMottaker;
    this.bruker = bruker;
    this.tema = tema;
    this.behandlingstema = behandlingstema;
    this.tittel = tittel;
    this.kanal = kanal;
    this.journalfoerendeEnhet = journalfoerendeEnhet;
    this.eksternReferanseId = eksternReferanseId;
    this.tilleggsopplysninger = tilleggsopplysninger;
    this.sak = sak;
    this.dokumenter = dokumenter;
  }
}
