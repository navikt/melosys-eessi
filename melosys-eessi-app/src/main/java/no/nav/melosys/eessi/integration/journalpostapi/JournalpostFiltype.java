package no.nav.melosys.eessi.integration.journalpostapi;

import com.google.common.collect.ImmutableMap;

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
  JPG,
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

  private static final Map<String, JournalpostFiltype> FILENDELSE_FILTYPE_MAP = Arrays.stream(JournalpostFiltype.values())
          .collect(Collectors.toMap(JournalpostFiltype::name, v -> v));

  private static final Map<String, JournalpostFiltype> MIMETYPE_FILTYPE_MAP = ImmutableMap.<String, JournalpostFiltype>builder()
          .put("application/pdf", PDF)
          .put("image/jpg", JPG)
          .put("image/jpeg", JPEG)
          .put("image/png", PNG)
          .build();


  public static Optional<JournalpostFiltype> fraMimeOgFilnavn(String mimeType, String filnavn) {
    if (MIMETYPE_FILTYPE_MAP.containsKey(mimeType)) {
      return Optional.of(MIMETYPE_FILTYPE_MAP.get(mimeType));
    }

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
    return FILENDELSE_FILTYPE_MAP.containsKey(filType);
  }
}
