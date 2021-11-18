package no.nav.melosys.eessi.models;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static no.nav.melosys.eessi.models.SedType.kreverAdresse;
import static org.assertj.core.api.Assertions.assertThat;

class SedTypeTest {

    @Test
    void kreverAdresse_verifiserer_ok() {
        Stream.of(SedType.A001, SedType.A002, SedType.A003, SedType.A004, SedType.A007, SedType.A009, SedType.A010)
            .forEach(sedType -> assertThat(kreverAdresse(sedType))
                .withFailMessage("SedType krever ikke adresse: '%s'", sedType)
                .isTrue());
    }
}
