package no.nav.melosys.eessi.models.buc;


import no.nav.melosys.eessi.models.sed.Konstanter;
import no.nav.melosys.eessi.models.sed.SED;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SedVersjonSjekkerTest {

    private final BUC buc = new BUC();
    private final SED sed = new SED();

    @Test
    void verifiserSedVersjonErBucVersjon_erLikVersjon_oppdateresIkke() {

        buc.setBucVersjon("v4.1");
        sed.setSedGVer("4");
        sed.setSedVer("1");

        SedVersjonSjekker.verifiserSedVersjonErBucVersjon(buc, sed);
        assertThat(sed.getSedGVer()).isEqualTo("4");
        assertThat(sed.getSedVer()).isEqualTo("1");
    }

    @Test
    void verifiserSedVersjonErBucVersjon_erForskjelligVersjon_oppdateres() {

        buc.setBucVersjon("v5.4");
        sed.setSedGVer("4");
        sed.setSedVer("1");

        SedVersjonSjekker.verifiserSedVersjonErBucVersjon(buc, sed);
        assertThat(sed.getSedGVer()).isEqualTo("5");
        assertThat(sed.getSedVer()).isEqualTo("4");
    }

    @Test
    void hentBucVersjon_riktigFormat_fungerer() {
        buc.setBucVersjon("v4.0");

        assertThat(SedVersjonSjekker.parseGVer(buc)).isEqualTo("4");
        assertThat(SedVersjonSjekker.parseVer(buc)).isEqualTo("0");
    }

    @Test
    void hentBucVersjon_uventetFormat_fårDefault() {
        buc.setBucVersjon("v21");

        assertThat(SedVersjonSjekker.parseGVer(buc)).isEqualTo(Konstanter.DEFAULT_SED_G_VER);
        assertThat(SedVersjonSjekker.parseVer(buc)).isEqualTo(Konstanter.DEFAULT_SED_VER);
    }

}
