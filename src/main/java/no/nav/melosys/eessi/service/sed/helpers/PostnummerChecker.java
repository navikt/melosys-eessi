package no.nav.melosys.eessi.service.sed.helpers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.exception.NotFoundException;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
public class PostnummerChecker {

    private static TreeMap<String, String> postnummerOgPoststed;

    static {
        String filename = "/kodeverk/postnummer.json";

        URL jsonUrl = PostnummerChecker.class.getResource(filename);

        try {
            postnummerOgPoststed = getTreeMapFromUrl(jsonUrl);
        } catch (IOException e) {
            log.error("Klarte ikke å lese postnummer fra fil {}. Error: {}", filename, e.getMessage());
        }
    }

    private static TreeMap<String, String> getTreeMapFromUrl(URL jsonUrl) throws IOException {
        return Arrays.stream(new ObjectMapper().readValue(jsonUrl, Postregister[].class))
                .collect(Collectors.toMap(
                        Postregister::getPostnummer,
                        Postregister::getPoststed,
                        (p1, p2) -> {
                            log.error("Duplikate postnummer funnet: {} og {}", p1, p2);
                            return p1;
                        },
                        TreeMap::new
                ));
    }

    public static String getPoststed(String postnummer) throws NotFoundException {

        String poststed = postnummerOgPoststed.get(postnummer);

        if (poststed == null) {
            throw new NotFoundException("Postnummer " + poststed + " ble ikke funnet.");
        }

        return poststed;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Postregister {

        private String postnummer;

        private String poststed;
    }
}
