package no.nav.melosys.eessi.service.sed.helpers;

import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class LandkodeMapper {

    private LandkodeMapper() {
    }

    private static final Map<String, String> ISO3_TIL_ISO2_LANDKODER_MAP = new HashMap<>();

    static {
        Arrays.stream(Locale.getISOCountries()).forEach(c -> ISO3_TIL_ISO2_LANDKODER_MAP.put(new Locale("", c).getISO3Country(), c));
        ISO3_TIL_ISO2_LANDKODER_MAP.put("XXX", "XS"); //Statsløs
        ISO3_TIL_ISO2_LANDKODER_MAP.put("???", "???"); //Ukjent
        ISO3_TIL_ISO2_LANDKODER_MAP.put("XUK", "???"); //Ukjent
    }

    public static String mapTilLandkodeIso2(String landkodeIso3) {
        return finnLandkodeIso2(landkodeIso3).orElse("???");
    }

    public static Optional<String> finnLandkodeIso2(String landkodeIso3) {
        if (landkodeIso3 == null) {
            return Optional.empty();
        }

        if (landkodeIso3.length() == 2) {
            return Optional.of(landkodeIso3);
        }

        return Optional.ofNullable(ISO3_TIL_ISO2_LANDKODER_MAP.get(landkodeIso3));
    }

    public static String finnLandkodeIso3(String landkodeIso2, boolean ukjentLandkodeMedPdlFormat ) {
        if (landkodeIso2 == null) {
            return null;
        }

        if (landkodeIso2.length() == 3) {
            return landkodeIso2;
        }
        return ISO3_TIL_ISO2_LANDKODER_MAP.entrySet().stream()
            .filter(entry -> entry.getValue().equals(landkodeIso2))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(ukjentLandkodeMedPdlFormat ? "XUK" : "???");
    }

    public static String mapTilNavLandkode(String landkode) {
        if ("UK".equalsIgnoreCase(landkode)) {
            return "GB";
        } else if ("EL".equalsIgnoreCase(landkode)) {
            return "GR";
        }
        return landkode;
    }
}
