package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Avslutning {

    private String dato;

    private Aarsak aarsak;

    private String type;
}
