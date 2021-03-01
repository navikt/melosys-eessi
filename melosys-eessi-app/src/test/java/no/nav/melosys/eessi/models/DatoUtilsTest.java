package no.nav.melosys.eessi.models;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import static no.nav.melosys.eessi.models.DatoUtils.tilLocalDate;
import static org.assertj.core.api.Assertions.assertThat;

class DatoUtilsTest {

    @Test
    void tilLocalDate_stringMedTime_parserTilLocaldate() {
        final String input = "2020-01-01:+02:00";
        final LocalDate forventet = LocalDate.of(2020, 1, 1);
        assertThat(tilLocalDate(input)).isEqualTo(forventet);

    }
}
