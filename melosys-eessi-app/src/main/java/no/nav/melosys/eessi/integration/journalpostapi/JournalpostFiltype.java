package no.nav.melosys.eessi.integration.journalpostapi;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

// https://confluence.adeo.no/display/BOA/Filtype
public enum JournalpostFiltype {
  PDF,
  PDFA,
  XML,
  RTF,
  DLF,
  JPEG,
  TIFF,
  AXML,
  DXML,
  JSON,
  PNG;

  private static final Map<String, JournalpostFiltype> FILENDELSE_FILTYPE_MAP = Arrays.stream(JournalpostFiltype.values())
          .collect(Collectors.toMap(JournalpostFiltype::name, v -> v));

  private static final Map<String, JournalpostFiltype> MIMETYPE_FILTYPE_MAP = ImmutableMap.<String, JournalpostFiltype>builder()
          .put("application/pdf", PDF)
          .put("image/jpg", JPEG)
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

  public static boolean erGyldigFiltypeForVariantformatArkiv(JournalpostFiltype journalpostFiltype) {
    return journalpostFiltype == PDF || journalpostFiltype == PDFA;
  }

  private static String transform(String extension){
    return "JPG".equals(extension) ? JPEG.name() : extension;
  }

  private static boolean contains(String filType) {
    return FILENDELSE_FILTYPE_MAP.containsKey(filType);
  }
}
