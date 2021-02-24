package no.nav.melosys.eessi.integration.pdl.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Value;

@Getter
@Value
public class PDLSokCriteria {
    @JsonProperty("fieldName")
    String feltNavn;
    @JsonProperty("searchRule")
    Map<String, String> søkeRegel;

    public static Builder fornavn() {
        return new Builder("person.navn.fornavn");
    }

    public static Builder etternavn() {
        return new Builder("person.navn.etternavn");
    }

    public static Builder fødselsdato() {
        return new Builder("person.foedsel.foedselsdato");
    }

    public static Builder statsborgerskap() {
        return new Builder("person.statsborgerskap.land");
    }

    public static class Builder {

        private final String feltNavn;
        private final Map<String, String> søkeRegel;

        private static final String INNEHOLDER = "contains";
        private static final String ER_LIK = "equals";

        private Builder(String feltNavn) {
            this.feltNavn = feltNavn;
            this.søkeRegel = new HashMap<>();
        }

        public PDLSokCriteria inneholder(String verdi) {
            søkeRegel.put(INNEHOLDER, verdi);
            return build();
        }

        public PDLSokCriteria erLik(String verdi) {
            søkeRegel.put(ER_LIK, verdi);
            return build();
        }

        private PDLSokCriteria build() {
            return new PDLSokCriteria(feltNavn, søkeRegel);
        }
    }
}
