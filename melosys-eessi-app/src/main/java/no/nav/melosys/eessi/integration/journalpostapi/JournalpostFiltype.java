package no.nav.melosys.eessi.integration.journalpostapi;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

  private static final Map<String, JournalpostFiltype> JOURNALPOST_FILTYPE_MAP = Arrays.stream(JournalpostFiltype.values())
          .collect(Collectors.toMap(JournalpostFiltype::name, v -> v));

  public static Optional<JournalpostFiltype> filnavn(String filnavn) {
    return Optional.ofNullable(filnavn)
        .filter(s -> s.contains(".") && s.lastIndexOf('.') + 1 < s.length())
        .map(s -> s.substring(s.lastIndexOf('.') + 1))
        .map(String::toUpperCase)
        .map(JournalpostFiltype::transform)
        .filter(JournalpostFiltype::contains)
        .map(JournalpostFiltype::valueOf);
  }

  private static String transform(String extension){
    return "JPG".equals(extension) ? JPEG.name() : extension;
  }

  private static boolean contains(String filType) {
    return JOURNALPOST_FILTYPE_MAP.containsKey(filType);
  }
}