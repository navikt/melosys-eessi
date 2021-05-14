package no.nav.melosys.eessi.models.buc;

import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.sed.SED;

import static no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_G_VER;
import static no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_VER;

@Slf4j
public class SedVersjonUtils {

    private SedVersjonUtils() {
        throw new IllegalStateException("Utility");
    }

    private static final Pattern VERSJON_PATTERN = Pattern.compile("^v(\\d)\\.(\\d)$");

    public static void verifiserSedVersjonErBucVersjon(BUC buc, SED sed) {
        final var ønsketSedVersjon = String.format("v%s.%s", sed.getSedGVer(), sed.getSedVer());

        if (!ønsketSedVersjon.equalsIgnoreCase(buc.getBucVersjon())) {
            log.info("Rina-sak {} er på gammel versjon {}. Oppdaterer SED til å bruke gammel versjon", buc.getId(), buc.getBucVersjon());
            sed.setSedGVer(SedVersjonUtils.parseGVer(buc));
            sed.setSedVer(SedVersjonUtils.parseVer(buc));
        }
    }

    static String parseGVer(BUC buc) {
        var matcher = VERSJON_PATTERN.matcher(buc.getBucVersjon());
        return matcher.find() ? matcher.group(1) : DEFAULT_SED_G_VER;
    }

    static String parseVer(BUC buc) {
        var matcher = VERSJON_PATTERN.matcher(buc.getBucVersjon());
        return matcher.find() ? matcher.group(2) : DEFAULT_SED_VER;
    }

}
