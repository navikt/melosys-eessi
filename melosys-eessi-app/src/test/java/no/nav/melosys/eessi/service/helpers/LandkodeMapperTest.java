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
    void getIso3_ikkeFunnet_girUkjent() {
        assertThat(LandkodeMapper.mapTilLandkodeIso2("ABC")).isEqualTo("???");
    }

    @Test
    void getIso2_medIkkeISOStandardKoder_forventSammeKodeTilbake() {
        assertThat(LandkodeMapper.mapTilLandkodeIso2("???")).isEqualTo("???");
        assertThat(LandkodeMapper.mapTilLandkodeIso2("XXX")).isEqualTo("XS");
        assertThat(LandkodeMapper.mapTilLandkodeIso2("XUK")).isEqualTo("???");
    }

    @Test
    public void skalReturnereISO3KodeForGyldigISO2Kode() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisasjon("US", true)).isEqualTo("USA");
    }

    @Test
    public void skalReturnereSammeKodeForISO3Kode() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisasjon("USA", true)).isEqualTo("USA");
    }

    @Test
    public void skalReturnereUkjentForUgyldigISO2KodeFelleskodeverkFormat() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisasjon("ZZ", false)).isEqualTo("XUK");
    }

    @Test
    public void skalReturnereUkjentForUgyldigISO2KodePdlFormat() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisasjon("ZZ", true)).isNull();
    }

    @Test
    public void skalReturnereNullForNullInndata() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisasjon(null, true)).isEqualTo(null);
    }

    @Test
    public void skalReturnereUkjentForTomStrengMedFelleskodeverkFormat() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisasjon("", false)).isEqualTo("XUK");
    }

    @Test
    public void skalReturnereUkjentForTomStrengMedPdlFormat() {
        assertThat(LandkodeMapper.finnLandkodeIso3ForIdentRekvisasjon("", false)).isEqualTo("XUK");
    }
}
