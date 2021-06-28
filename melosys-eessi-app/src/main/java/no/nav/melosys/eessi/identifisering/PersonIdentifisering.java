package no.nav.melosys.eessi.identifisering;

import java.util.Optional;

import no.nav.melosys.eessi.models.sed.SED;

public interface PersonIdentifisering {
    Optional<String> identifiserPerson(String rinaSaksnumer, SED sed);
}
