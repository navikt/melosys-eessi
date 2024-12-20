package no.nav.melosys.eessi.service.sed.mapper.til_sed;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import no.nav.melosys.eessi.controller.dto.Adressetype;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.sed.nav.Adresse;
import no.nav.melosys.eessi.models.sed.nav.Arbeidsland;
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SedMapperTest {
    private final SedMapper sedMapper =  new TempTestSedMapper(); // fjerns når vi koverterer til Kotlin

    private SedDataDto sedData;

    @BeforeEach
    void setUp() throws IOException, URISyntaxException {
        sedData = SedDataStub.getStub();
    }

    @Test
    void hentAdresser() {
        final List<Adresse> adresser = sedMapper.hentAdresser(sedData);

        assertThat(adresser).hasSize(3)
            .anyMatch(adresse -> Adressetype.BOSTEDSADRESSE.getAdressetypeRina().equals(adresse.getType()))
            .anyMatch(adresse -> Adressetype.KONTAKTADRESSE.getAdressetypeRina().equals(adresse.getType()))
            .anyMatch(adresse -> Adressetype.POSTADRESSE.getAdressetypeRina().equals(adresse.getType()));
    }

    @Test
    void hentArbeidsland() {
        final List<Arbeidsland> arbeidsland = sedMapper.hentArbeidsland(sedData);

        assertThat(arbeidsland).hasSize(1);

        arbeidsland.stream().forEach(arbLand -> {
            assertThat(arbLand.getArbeidssted()).hasSize(1);
        });
    }

    @Test
    void hentStatsborgerskap() {
        final List<Statsborgerskap> statsborgerskap = sedMapper.hentStatsborgerskap(sedData);
        assertThat(statsborgerskap).hasSize(2).containsExactly(
            new Statsborgerskap("NO"),
            new Statsborgerskap("SE")
        );
    }
}
