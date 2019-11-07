package no.nav.melosys.eessi.service.sed.helpers;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.exception.NotFoundException;

@Slf4j
public final class LandkodeMapper {

    private LandkodeMapper() {}

    private static TreeMap<String, String> landkoderIso3OgIso2; // Key: iso3, Value: iso2

    static {
        String filename = "/kodeverk/landkoder.json";

        URL jsonUrl = LandkodeMapper.class.getResource(filename);

        try {
            landkoderIso3OgIso2 = getTreeMapFromUrl(jsonUrl);
        } catch (IOException e) {
            log.error("Klarte ikke å lese landkoder fra fil {}. Error {}", filename, e);
        }
    }

    private static TreeMap<String, String> getTreeMapFromUrl(URL jsonUrl) throws IOException {
        return Arrays.stream(new ObjectMapper().readValue(jsonUrl, Landkode[].class))
                .collect(Collectors.toMap(
                        Landkode::getIso3,
                        Landkode::getIso2,
                        (String l1, String  l2) -> {
                            log.error("Duplikate landkoder funnet: {} og {}", l1, l2);
                            return l1;
                        },
                        TreeMap::new
                ));
    }

    public static String getLandkodeIso2(String landkodeIso3) throws NotFoundException {
        if (landkodeIso3 == null) {
            return null;
        }

        if (landkodeIso3.length() == 2) {
            return landkodeIso3;
        }

        String landkodeIso2 = landkoderIso3OgIso2.get(landkodeIso3);

        if (landkodeIso2 == null) {
            throw new NotFoundException("Landkode " + landkodeIso3 + " ble ikke funnet.");
        }

        return landkodeIso2;
    }

    public static String mapTilEuLandkode(String landkode) {
        if ("GB".equalsIgnoreCase(landkode)) {
            return "UK";
        } else if ("EL".equalsIgnoreCase(landkode)) {
            return "GR";
        }
        return landkode;
    }

    public static String mapTilNavLandkode(String landkode) {
        if ("UK".equalsIgnoreCase(landkode)) {
            return "GB";
        } else if ("GR".equalsIgnoreCase(landkode)) {
            return "EL";
        }
        return landkode;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Landkode {

        private String iso2;

        private String iso3;
    }
}
