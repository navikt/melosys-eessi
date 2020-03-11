package no.nav.melosys.eessi.controller.dto;

import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
public enum Adressetype {
    BOSTEDSADRESSE("bosted"),
    POSTADRESSE("opphold"),
    KONTAKTADRESSE("kontakt"),
    ANNET("annet");

    @Getter
    private final String adressetypeRina;

    public static Adressetype fraAdressetypeRina(String adressetypeRina) {
        if (StringUtils.isEmpty(adressetypeRina)) {
            return ANNET;
        }

        return Stream.of(values())
                .filter(adressetype -> adressetype.adressetypeRina.equalsIgnoreCase(adressetypeRina))
                .findFirst().orElse(ANNET);
    }
}
