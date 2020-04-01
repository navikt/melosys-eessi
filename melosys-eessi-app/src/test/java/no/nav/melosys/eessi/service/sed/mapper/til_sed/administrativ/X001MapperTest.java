package no.nav.melosys.eessi.service.sed.mapper.til_sed.administrativ;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class X001MapperTest {

    @Test
    public void mapFraSed() throws Exception {

        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA009.json");

        String sedString = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));
        SED fraSed = new ObjectMapper().readValue(sedString, SED.class);

        X001Mapper mapper = new X001Mapper();
        SED x001 = mapper.mapFraSed(fraSed, "aarsaken");

        assertThat(x001).isNotNull();
        assertThat(x001.getMedlemskap()).isNull();
        assertThat(x001.getSedType()).isEqualTo(SedType.X001.name());

        //Påkrevde felter
        assertThat(x001.getNav().getSak().getKontekst().getBruker().getPerson().getFornavn()).isNotNull();
        assertThat(x001.getNav().getSak().getKontekst().getBruker().getPerson().getEtternavn()).isNotNull();
        assertThat(x001.getNav().getSak().getKontekst().getBruker().getPerson().getFoedselsdato()).isNotNull();
        assertThat(x001.getNav().getSak().getAnmodning().getAvslutning().getDato()).isNotNull();
        assertThat(x001.getNav().getSak().getAnmodning().getAvslutning().getType()).isEqualTo("automatisk");
        assertThat(x001.getNav().getSak().getAnmodning().getAvslutning().getAarsak().getType()).isEqualTo("aarsaken");
    }
}
