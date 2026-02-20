package no.nav.melosys.eessi.service.sed.mapper.til_sed.administrativ;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import io.getunleash.FakeUnleash;
import no.nav.melosys.eessi.config.featuretoggle.ToggleName;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class X001MapperTest {

    private final FakeUnleash fakeUnleash = new FakeUnleash();

    @Test
    void mapFraSed_cdm43() throws Exception {
        fakeUnleash.disable(ToggleName.CDM_4_4);

        SED fraSed = lesSed();
        X001Mapper mapper = new X001Mapper(fakeUnleash);
        SED x001 = mapper.mapFraSed(fraSed, "aarsaken");

        assertThat(x001).extracting(SED::getSedType, SED::getMedlemskap, SED::getSedGVer, SED::getSedVer)
            .containsExactly(SedType.X001.name(), null, "4", "3");

        verifiserPåkrevdeFelter(x001);
    }

    @Test
    void mapFraSed_cdm44() throws Exception {
        fakeUnleash.enable(ToggleName.CDM_4_4);

        SED fraSed = lesSed();
        X001Mapper mapper = new X001Mapper(fakeUnleash);
        SED x001 = mapper.mapFraSed(fraSed, "aarsaken");

        assertThat(x001).extracting(SED::getSedType, SED::getMedlemskap, SED::getSedGVer, SED::getSedVer)
            .containsExactly(SedType.X001.name(), null, "4", "4");

        verifiserPåkrevdeFelter(x001);
    }

    private SED lesSed() throws Exception {
        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA009.json");
        String sedString = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));
        return JsonMapper.builder().build().readValue(sedString, SED.class);
    }

    private void verifiserPåkrevdeFelter(SED x001) {
        assertThat(x001.getNav().getSak().getKontekst().getBruker().getPerson().getFornavn()).isNotNull();
        assertThat(x001.getNav().getSak().getKontekst().getBruker().getPerson().getEtternavn()).isNotNull();
        assertThat(x001.getNav().getSak().getKontekst().getBruker().getPerson().getFoedselsdato()).isNotNull();
        assertThat(x001.getNav().getSak().getAnmodning().getAvslutning().getDato()).isNotNull();
        assertThat(x001.getNav().getSak().getAnmodning().getAvslutning().getType()).isEqualTo("automatisk");
        assertThat(x001.getNav().getSak().getAnmodning().getAvslutning().getAarsak().getType()).isEqualTo("aarsaken");
    }
}
