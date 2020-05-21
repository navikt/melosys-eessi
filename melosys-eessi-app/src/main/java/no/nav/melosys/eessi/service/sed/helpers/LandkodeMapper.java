package no.nav.melosys.eessi.service.sed.helpers;

import java.util.Arrays;
import java.util.Locale;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.exception.NotFoundException;

@Slf4j
public final class LandkodeMapper {

    private LandkodeMapper() {}

    private static BiMap<String, String> map = HashBiMap.create();

    static {
        Arrays.stream(Locale.getISOCountries()).forEach(c -> map.put(new Locale("", c).getISO3Country(), c));
        //FIXME: midlertidig fix for landkoder utenfor iso-standard til vi blir enige om hvilke som skal brukes
        map.put("XXX", "XXX"); //Statsløs
        map.put("???", "???"); //Ukjent
    }

    public static String getLandkodeIso2(String landkodeIso3) {
        if (landkodeIso3 == null) {
            return null;
        }

        if (landkodeIso3.length() == 2) {
            return landkodeIso3;
        }

        String landkodeIso2 = map.get(landkodeIso3);

        if (landkodeIso2 == null) {
            throw new NotFoundException("Landkode " + landkodeIso3 + " ble ikke funnet.");
        }

        return landkodeIso2;
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
