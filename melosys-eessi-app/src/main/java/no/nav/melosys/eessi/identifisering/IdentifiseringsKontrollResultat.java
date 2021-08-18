package no.nav.melosys.eessi.identifisering;

import java.util.Collection;
import java.util.stream.Collectors;

import lombok.Value;

@Value
public class IdentifiseringsKontrollResultat {

    private static final String FEIL_BESKRIVELSE = "Feil i opplysninger av identifisert person: %s";

    Collection<IdentifiseringsKontrollBegrunnelse> begrunnelser;

    public boolean erIdentifisert() {
        return begrunnelser.isEmpty();
    }

    public String hentFeilIOpplysningerTekst() {
        String manglerTekst = begrunnelser.stream()
            .map(IdentifiseringsKontrollBegrunnelse::getBegrunnelse)
            .collect(Collectors.joining(", "));

        return String.format(FEIL_BESKRIVELSE, manglerTekst);
    }
}
