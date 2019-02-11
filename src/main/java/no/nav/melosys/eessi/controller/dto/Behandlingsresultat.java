package no.nav.melosys.eessi.controller.dto;

import lombok.Data;
import no.nav.melosys.eessi.models.sed.nav.Land;

import java.util.Set;

@Data
public class Behandlingsresultat {

    private Set<Lovvalgsperiode> lovvalgsperioder;

    private Land fastsattAvLand;
}
