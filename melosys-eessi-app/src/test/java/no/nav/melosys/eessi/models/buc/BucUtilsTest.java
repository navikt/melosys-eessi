package no.nav.melosys.eessi.models.buc;


import no.nav.melosys.eessi.models.sed.Konstanter;
import no.nav.melosys.eessi.models.sed.SED;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BucUtilsTest {

    private BUC buc = new BUC();
    private SED sed = new SED();

    @Test
    public void verifiserSedVersjonErBucVersjon_erLikVersjon_oppdateresIkke() {

        buc.setBucVersjon("v4.1");
        sed.setSedGVer("4");
        sed.setSedVer("1");

        BucUtils.verifiserSedVersjonErBucVersjon(buc, sed);
        assertThat(sed.getSedGVer()).isEqualTo("4");
        assertThat(sed.getSedVer()).isEqualTo("1");
    }

    @Test
    public void verifiserSedVersjonErBucVersjon_erForskjelligVersjon_oppdateres() {

        buc.setBucVersjon("v5.4");
        sed.setSedGVer("4");
        sed.setSedVer("1");

        BucUtils.verifiserSedVersjonErBucVersjon(buc, sed);
        assertThat(sed.getSedGVer()).isEqualTo("5");
        assertThat(sed.getSedVer()).isEqualTo("4");
    }

    @Test
    public void hentBucVersjon_riktigFormat_fungerer() {
        buc.setBucVersjon("v4.0");

        assertThat(BucUtils.parseGVer(buc)).isEqualTo("4");
        assertThat(BucUtils.parseVer(buc)).isEqualTo("0");
    }

    @Test
    public void hentBucVersjon_uventetFormat_fårDefault() {
        buc.setBucVersjon("v21");

        assertThat(BucUtils.parseGVer(buc)).isEqualTo(Konstanter.DEFAULT_SED_G_VER);
        assertThat(BucUtils.parseVer(buc)).isEqualTo(Konstanter.DEFAULT_SED_VER);
    }

}
