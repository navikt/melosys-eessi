package no.nav.melosys.eessi.service.identifisering;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FnrUtilsTest {

    @Test
    void filtrerUtGyldigNorskIdent_nullverdi_tomtResultat() {
        assertThat(FnrUtils.filtrerUtGyldigNorskIdent(null)).isNotPresent();
    }

    @Test
    void filtrerUtGyldigNorskIdent_ikkeGyldigIdent_tomtResultat() {
        assertThat(FnrUtils.filtrerUtGyldigNorskIdent("123-lala-22")).isNotPresent();
    }

    @Test
    void filtrerUtGyldigNorskIdent_gyldigFnrMedBindestrekMellom_returnererKorrektIdent() {
        assertThat(FnrUtils.filtrerUtGyldigNorskIdent("2506-84-20779 ")).contains("25068420779");
    }

    @Test
    void filtrerUtGyldigNorskIdent_gyldigDnrMedIkkeNumeriskeTegnkMellom_returnererKorrektIdent() {
        assertThat(FnrUtils.filtrerUtGyldigNorskIdent("64068-648....643")).contains("64068648643");
    }

    @Test
    void fjernIkkeNumeriskeTegn_medDiverseTegnMellomNummer_fjernerAlleIkkeNumeriske() {
        assertThat(FnrUtils.fjernIkkeNumeriskeTegn("123321.")).isEqualTo("123321");
        assertThat(FnrUtils.fjernIkkeNumeriskeTegn("1e2r33-2.1.")).isEqualTo("123321");
        assertThat(FnrUtils.fjernIkkeNumeriskeTegn("1e2 r33-2.1.")).isEqualTo("123321");
        assertThat(FnrUtils.fjernIkkeNumeriskeTegn("abc.-?0%#&%$")).isEqualTo("0");
    }
}
