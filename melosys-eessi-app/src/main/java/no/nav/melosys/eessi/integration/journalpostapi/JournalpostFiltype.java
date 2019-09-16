package no.nav.melosys.eessi.integration.journalpostapi;

import java.util.Arrays;
import java.util.Optional;

public enum JournalpostFiltype {

  PDF,
  PDFA,
  XML,
  RTF,
  AFP,
  META,
  DLF,
  JPEG,
  TIFF,
  DOC,
  DOCX,
  XLS,
  XLSX,
  AXML,
  DXML,
  JSON,
  PNG;

  public static Optional<JournalpostFiltype> filnavn(String filnavn) {
    return Optional.ofNullable(filnavn)
        .filter(s -> s.contains(".") && s.lastIndexOf(".") < s.length())
        .map(s -> s.substring(s.lastIndexOf(".") + 1))
        .map(String::toUpperCase)
        .map(JournalpostFiltype::transform)
        .filter(JournalpostFiltype::contains)
        .map(JournalpostFiltype::valueOf);
  }

  private static String transform(String extension){
    return "JPG".equals(extension) ? JPEG.name() : extension;
  }

  private static boolean contains(String value) {
    return Arrays.stream(JournalpostFiltype.values())
        .anyMatch(journalpostFiltype -> journalpostFiltype.name().equalsIgnoreCase(value));
  }

}