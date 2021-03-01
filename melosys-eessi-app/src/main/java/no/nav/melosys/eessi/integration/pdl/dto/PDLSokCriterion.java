package no.nav.melosys.eessi.integration.pdl.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Value;

@Getter
@Value
public class PDLSokCriterion {
    String fieldName;
    Map<String, Object> searchRule;

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
        private final Map<String, Object> søkeRegel;

        private static final String INNEHOLDER = "contains";
        private static final String ER_LIK = "equals";

        private Builder(String feltNavn) {
            this.feltNavn = feltNavn;
            this.søkeRegel = new HashMap<>();
        }

        public PDLSokCriterion inneholder(Object verdi) {
            søkeRegel.put(INNEHOLDER, verdi);
            return build();
        }

        public PDLSokCriterion erLik(Object verdi) {
            søkeRegel.put(ER_LIK, verdi);
            return build();
        }

        private PDLSokCriterion build() {
            return new PDLSokCriterion(feltNavn, søkeRegel);
        }
    }
}
