package no.nav.melosys.eessi.service.sed.helpers;

import java.util.*;

import lombok.extern.slf4j.Slf4j;

/**
 * Mapper mellom landkoder i ISO2 og ISO3 format.
 * <p>
 * Legg merke til kode for ukjent land der vi bruker XUK for ISO3 og XU for ISO2. Disse kodene er ikke i bruk i ISO-standarden,
 * men er valgt på grunn av at de er i bruk i PDL og Rina. Disse gjenbruker vi i Melosys for å unngå å måtte lage egne.
 */
@Slf4j
public final class LandkodeMapper {

    private LandkodeMapper() {
    }

    private static final String UKJENT_LANDKODE_ISO3 = "XUK";

    private static final String UKJENT_LANDKODE_ISO2 = "XU";

    private static final Map<String, String> ISO3_TIL_ISO2_LANDKODER_MAP = new HashMap<>();

    static {
        Arrays.stream(Locale.getISOCountries()).forEach(c -> ISO3_TIL_ISO2_LANDKODER_MAP.put(new Locale("", c).getISO3Country(), c));
        ISO3_TIL_ISO2_LANDKODER_MAP.put("XXX", "XS");
        ISO3_TIL_ISO2_LANDKODER_MAP.put(UKJENT_LANDKODE_ISO3, UKJENT_LANDKODE_ISO2);
    }

    public static String mapTilLandkodeIso2(String landkodeIso3) {
        return finnLandkodeIso2(landkodeIso3).orElse(UKJENT_LANDKODE_ISO2);
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

    public static String finnLandkodeIso3ForIdentRekvisisjon(String landkodeIso2, boolean skalReturnereNullForUkjent) {
        if (landkodeIso2 == null) {
            return null;
        }

        if (landkodeIso2.length() == 3) {
            return landkodeIso2;
        }
        return ISO3_TIL_ISO2_LANDKODER_MAP.entrySet().stream()
            .filter(entry -> entry.getValue().equals(mapTilNavLandkode(landkodeIso2)))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(skalReturnereNullForUkjent ? null : UKJENT_LANDKODE_ISO3);
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
