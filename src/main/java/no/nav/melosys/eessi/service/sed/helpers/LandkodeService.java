package no.nav.melosys.eessi.service.sed.helpers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.exception.LandkodeIkkeFunnetException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LandkodeService {

    private TreeMap<String, String> landkoderIso3OgIso2; // Key: iso3, Value: iso2

    public LandkodeService() {
        String filename = "/kodeverk/landkoder.json";

        URL jsonUrl = getClass().getResource(filename);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Henter inn listen fra json og konverterer den til en TreeMap
            landkoderIso3OgIso2 = Arrays.stream(objectMapper.readValue(jsonUrl, Landkode[].class))
                    .collect(Collectors.toMap(
                            Landkode::getIso3,
                            Landkode::getIso2,
                            (l1, l2) -> {
                                log.error("Duplikate landkoder funnet: {} og {}", l1, l2);
                                return l1;
                            },
                            TreeMap::new
                    ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLandkodeIso2(String landkodeIso3) throws LandkodeIkkeFunnetException {
        String landkodeIso2 = landkoderIso3OgIso2.get(landkodeIso3);

        if (landkodeIso2 == null) {
            throw new LandkodeIkkeFunnetException("Landkode " + landkodeIso3 + " ble ikke funnet.");
        }

        return landkodeIso2;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Landkode {

        private String iso2;

        private String iso3;
    }
}
