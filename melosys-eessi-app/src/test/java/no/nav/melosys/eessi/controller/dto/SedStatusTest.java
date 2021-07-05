package no.nav.melosys.eessi.controller.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class SedStatusTest {

    @Test
    void fraNorskStatus_medGyldigeStatuser_forventRettStatus() {
        String tom = "tom";
        String utkast = "utkast";

        assertThat(SedStatus.fraNorskStatus(tom)).isEqualTo(SedStatus.TOM);
        assertThat(SedStatus.fraNorskStatus(utkast)).isEqualTo(SedStatus.UTKAST);
    }

    @Test
    void fraNorskStatus_medUgyldigStatus_forventException() {
        String ugyldigStatus = "abc123";
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> SedStatus.fraNorskStatus(ugyldigStatus));
    }

    @Test
    void fraNorskStatus_medTomStatus_forventNull() {
        String tomStatus = "";

        assertThat(SedStatus.fraNorskStatus(tomStatus)).isNull();
        assertThat(SedStatus.fraNorskStatus(null)).isNull();
    }

    @Test
    void fraEngelskStatus_medGyldigeStatuser_forventRettStatus() {
        String empty = "empty";
        String statusNew = "new";

        assertThat(SedStatus.fraEngelskStatus(empty)).isEqualTo(SedStatus.TOM);
        assertThat(SedStatus.fraEngelskStatus(statusNew)).isEqualTo(SedStatus.UTKAST);
    }

    @Test
    void fraEngelskStatus_medUgyldigStatus_forventNull() {
        String ugyldigStatus = "abc123";

        assertThat(SedStatus.fraEngelskStatus(ugyldigStatus)).isNull();
    }

    @Test
    void fraEngelskStatus_medTomStatus_forventNull() {
        String tomStatus = "";

        assertThat(SedStatus.fraEngelskStatus(tomStatus)).isNull();
        assertThat(SedStatus.fraEngelskStatus(null)).isNull();
    }

    @Test
    void erGyldigStatus() {
        assertThat(SedStatus.erGyldigEngelskStatus("empty")).isFalse();
        assertThat(SedStatus.erGyldigEngelskStatus("sent")).isTrue();
        assertThat(SedStatus.erGyldigEngelskStatus("new")).isFalse();
        assertThat(SedStatus.erGyldigEngelskStatus("received")).isTrue();
    }
}
