package no.nav.melosys.eessi.service.sed.helpers;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.exception.NotFoundException;

@Slf4j
public final class LandkodeMapper {

    private LandkodeMapper() {}

    private static final BiMap<String, String> ISO3_ISO2_LANDKODER_MAP = HashBiMap.create();

    static {
        Arrays.stream(Locale.getISOCountries()).forEach(c -> ISO3_ISO2_LANDKODER_MAP.put(new Locale("", c).getISO3Country(), c));
        ISO3_ISO2_LANDKODER_MAP.put("XXX", "XS"); //Statsløs
        ISO3_ISO2_LANDKODER_MAP.put("???", "???"); //Ukjent
        ISO3_ISO2_LANDKODER_MAP.put("XUK", "XUK"); //Ukjent
    }

    public static String mapTilLandkodeIso2(String landkodeIso3) {
        return finnLandkodeIso2(landkodeIso3).orElseThrow(() -> new NotFoundException("Landkode " + landkodeIso3 + " ble ikke funnet."));
    }

    public static Optional<String> finnLandkodeIso2(String landkodeIso3) {
        if (landkodeIso3 == null) {
            return Optional.empty();
        }

        if (landkodeIso3.length() == 2) {
            return Optional.of(landkodeIso3);
        }

        return Optional.ofNullable(ISO3_ISO2_LANDKODER_MAP.get(landkodeIso3));
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
