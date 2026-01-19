package no.nav.melosys.eessi.service.sed.mapper.til_sed.administrativ;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class X001MapperTest {

    @Test
    void mapFraSed() throws Exception {

        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA009.json");

        String sedString = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));
        SED fraSed = JsonMapper.builder().build().readValue(sedString, SED.class);

        X001Mapper mapper = new X001Mapper();
        SED x001 = mapper.mapFraSed(fraSed, "aarsaken");

        assertThat(x001).extracting(SED::getSedType, SED::getMedlemskap, SED::getSedGVer, SED::getSedVer)
            .containsExactly(SedType.X001.name(), null, "4", "3");

        //Påkrevde felter
        assertThat(x001.getNav().getSak().getKontekst().getBruker().getPerson().getFornavn()).isNotNull();
        assertThat(x001.getNav().getSak().getKontekst().getBruker().getPerson().getEtternavn()).isNotNull();
        assertThat(x001.getNav().getSak().getKontekst().getBruker().getPerson().getFoedselsdato()).isNotNull();
        assertThat(x001.getNav().getSak().getAnmodning().getAvslutning().getDato()).isNotNull();
        assertThat(x001.getNav().getSak().getAnmodning().getAvslutning().getType()).isEqualTo("automatisk");
        assertThat(x001.getNav().getSak().getAnmodning().getAvslutning().getAarsak().getType()).isEqualTo("aarsaken");
    }
}
