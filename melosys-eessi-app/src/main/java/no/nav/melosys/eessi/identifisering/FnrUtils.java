package no.nav.melosys.eessi.identifisering;

import java.util.Optional;

import no.bekk.bekkopen.person.FodselsnummerValidator;

public final class FnrUtils {
    private FnrUtils() {}

    /**
     * Tar hensyn til ikke-numeriske tegn oppgitt som ident
     */
    public static Optional<String> filtrerUtGyldigNorskIdent(String ident) {
        return Optional.ofNullable(ident)
                .map(FnrUtils::fjernIkkeNumeriskeTegn)
                .filter(FnrUtils::erGyldigIdent);
    }

    static String fjernIkkeNumeriskeTegn(String tekst) {
        return tekst.replaceAll("[^\\d]", "");
    }

    private static boolean erGyldigIdent(String ident) {
        return FodselsnummerValidator.isValid(ident);
    }
}
