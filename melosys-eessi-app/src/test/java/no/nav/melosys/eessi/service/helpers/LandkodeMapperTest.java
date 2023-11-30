package no.nav.melosys.eessi.service.helpers;

import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class LandkodeMapperTest {

    @Test
    void getIso2_expectIso2() {
        assertThat(LandkodeMapper.mapTilLandkodeIso2("NOR")).isEqualTo("NO");
        assertThat(LandkodeMapper.mapTilLandkodeIso2("SWE")).isEqualTo("SE");
        assertThat(LandkodeMapper.mapTilLandkodeIso2("DNK")).isEqualTo("DK");
    }

    @Test
    void getIso2_withIso2_expectIso2() {
        assertThat(LandkodeMapper.mapTilLandkodeIso2("NO")).isEqualTo("NO");
        assertThat(LandkodeMapper.mapTilLandkodeIso2("SE")).isEqualTo("SE");
        assertThat(LandkodeMapper.mapTilLandkodeIso2("DK")).isEqualTo("DK");
    }


    @Test
    void getIso2_medIkkeISOStandardKoder_forventSammeKodeTilbake() {
        assertThat(LandkodeMapper.mapTilLandkodeIso2("???")).isEqualTo("XU");
        assertThat(LandkodeMapper.mapTilLandkodeIso2("XXX")).isEqualTo("XS");
        assertThat(LandkodeMapper.mapTilLandkodeIso2("XUK")).isEqualTo("XU");
    }

    @Test
    void skalReturnereISO3KodeForGyldigISO2Kode() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("US", true)).isEqualTo("USA");
    }

    @Test
    void finnLandkodeIso3ForIdentRekvisisjon_ikkeFunnet_girUkjent() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("AB", false)).isEqualTo("XUK");
    }

    @Test
    void finnLandkodeIso3ForIdentRekvisisjon_ikkeFunnet_girNull() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("AB", true)).isEqualTo(null);
    }

    @Test
    void skalReturnereSammeKodeForISO3Kode() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("USA", true)).isEqualTo("USA");
    }

    @Test
    void skalReturnereUkjentForUgyldigISO2KodeFelleskodeverkFormat() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("ZZ", false)).isEqualTo("XUK");
    }

    @Test
    void skalReturnereUkjentForUgyldigISO2KodePdlFormat() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("ZZ", true)).isNull();
    }

    @Test
    void skalReturnereNullForNullInndata() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon(null, true)).isNull();
    }

    @Test
    void skalReturnereUkjentForTomStrengMedFelleskodeverkFormat() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("", false)).isEqualTo("XUK");
    }

    @Test
    void skalReturnereUkjentForTomStrengMedPdlFormat() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("", false)).isEqualTo("XUK");
    }

    @Test
    void skalReturnereGBsinISO3ForUK() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("UK", false)).isEqualTo("GBR");
    }

    @Test
    void skalReturnereGRsinISO3IForEL() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisisjon("EL", false)).isEqualTo("GRC");
    }
}
