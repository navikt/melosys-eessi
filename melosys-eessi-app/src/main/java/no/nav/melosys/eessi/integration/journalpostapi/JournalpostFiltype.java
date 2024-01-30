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
    DOCX,
    PNG;

  private static final Map<String, JournalpostFiltype> FILENDELSE_FILTYPE_MAP = Arrays.stream(JournalpostFiltype.values())
          .collect(Collectors.toMap(JournalpostFiltype::name, v -> v));

  private static final Map<String, JournalpostFiltype> MIMETYPE_FILTYPE_MAP = ImmutableMap.<String, JournalpostFiltype>builder()
          .put("application/pdf", PDF)
          .put("image/jpg", JPEG)
          .put("image/jpeg", JPEG)
          .put("image/png", PNG)
          .build();

    private static final Map<String, JournalpostFiltype> MIMETYPE_FILTYPE_MAP_NY = ImmutableMap.<String, JournalpostFiltype>builder()
        .put("application/pdf", PDF)
        .put("image/jpg", JPEG)
        .put("image/jpeg", JPEG)
        .put("image/png", PNG)
        .put("image/tiff", TIFF)
        .put("application/vnd.openxmlformats-officedocument.wordprocessing", DOCX)
        .put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", DOCX)
        .build();



    public static Optional<JournalpostFiltype> fraMimeOgFilnavn(String mimeType, String filnavn ) {
        if (MIMETYPE_FILTYPE_MAP_NY.containsKey(mimeType)) {
            return Optional.of(MIMETYPE_FILTYPE_MAP_NY.get(mimeType));
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
