package no.nav.melosys.eessi.service.sed.helpers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ErOpprinneligVedtakMapperTest {

    @ParameterizedTest
    @ValueSource(strings = {"nei", "NEI", "false", "FALSE"})
    void map_erOpprinneligVedtakMapper_medForskjelligCapitalization_medNeiOgFalse_returnerer_optional_false(String erOpprinneligVedtak) {
        var optionalErOpprinneligVedtak = ErOpprinneligVedtakMapper.map(erOpprinneligVedtak);

        assertThat(optionalErOpprinneligVedtak).isPresent().get().isEqualTo(false);
    }


    @ParameterizedTest
    @ValueSource(strings = {"ja", "JA", "true", "TRUE"})
    void map_erOpprinneligVedtakMapper_medForskjelligCapitalization_medJaOgTrue_returnerer_optional_true(String erOpprinneligVedtak) {
        var optionalErOpprinneligVedtak = ErOpprinneligVedtakMapper.map(erOpprinneligVedtak);

        assertThat(optionalErOpprinneligVedtak).isPresent().get().isEqualTo(true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Si", "Adksad", "", "null", " "})
    void map_erOpprinneligVedtakMapper_medUkjenteVerdier_returnerer_optional_tom(String erOpprinneligVedtak) {
        var optionalErOpprinneligVedtak = ErOpprinneligVedtakMapper.map(erOpprinneligVedtak);

        assertThat(optionalErOpprinneligVedtak).isNotPresent();
    }

    @Test
    void map_erOpprinneligVedtakMapper_medNull_returnerer_optional_tom() {
        String erOpprinneligVedtak = null;

        var optionalErOpprinneligVedtak = ErOpprinneligVedtakMapper.map(erOpprinneligVedtak);

        assertThat(optionalErOpprinneligVedtak).isNotPresent();
    }
}
