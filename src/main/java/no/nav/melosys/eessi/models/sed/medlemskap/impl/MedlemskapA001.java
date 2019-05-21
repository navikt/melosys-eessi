package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.nav.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MedlemskapA001 extends Medlemskap {

    private Unntak unntak;

    private Vertsland vertsland;

    private Fastperiode soeknadsperiode;

    private List<Periode> tidligereperiode;

    private List<Land> naavaerendemedlemskap; // Landkode

    private List<Land> forespurtmedlemskap; // Landkode

    private Anmodning anmodning;

    private List<ForrigeSoeknad> forrigesoeknad;
}
