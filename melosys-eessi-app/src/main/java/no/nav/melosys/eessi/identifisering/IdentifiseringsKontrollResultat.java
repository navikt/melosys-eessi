package no.nav.melosys.eessi.identifisering;

import java.util.Collection;
import java.util.stream.Collectors;

import lombok.Value;

@Value
public class IdentifiseringsKontrollResultat {

    private static final String FEIL_BESKRIVELSE = "-- Automatisk SED-kontroll --\n" + "Feil i opplysninger av identifisert person: %s\n";
    private static final String OVERSTYRINGSMELDING = "Du kan overføre oppgaven hvis du ser en feil i mottatt SED. \nNB! Dette kan ikke omgjøres. Vennligst kontroller opplysningene før du overfører oppgaven.";

    Collection<IdentifiseringsKontrollBegrunnelse> begrunnelser;

    public boolean erIdentifisert() {
        return begrunnelser.isEmpty();
    }

    public String hentFeilIOpplysningerTekst() {
        String manglerTekst = begrunnelser.stream()
            .map(IdentifiseringsKontrollBegrunnelse::getBegrunnelse)
            .collect(Collectors.joining(", "));

        return String.format(FEIL_BESKRIVELSE, manglerTekst) + OVERSTYRINGSMELDING;
    }
}
