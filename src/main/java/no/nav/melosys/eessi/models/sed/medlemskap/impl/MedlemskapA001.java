package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.nav.*;

import java.util.Collection;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MedlemskapA001 extends Medlemskap {

    private Unntak unntak;

    private Vertsland vertsland;

    @JsonProperty("soeknadsperiode")
    private Fastperiode søknadsperiode;

    @JsonProperty("tidligereperiode")
    private Collection<Tidligereperiode> tidligereperiode;

    @JsonProperty("naavaerendemedlemskap")
    private Collection<Land> nåværendemedlemskap; // Landkode

    private Collection<Land> forespurtmedlemskap; // Landkode

    private Anmodning anmodning;

    @JsonProperty("datoforrigesoeknad")
    private String datoforrigesøknad;
}
