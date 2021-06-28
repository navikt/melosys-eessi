package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA011;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class A011MapperTest {

    private final A011Mapper a011Mapper = new A011Mapper();

    private SED a001;

    @BeforeEach
    public void setup() throws IOException {
        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA001.json");
        a001 = new ObjectMapper().readValue(jsonUrl, SED.class);
    }

    @Test
    void mapFraEksisterendeSedA001() {
        SED a011 = a011Mapper.mapFraSed(a001);

        assertThat(a011.getSedType()).isEqualToIgnoringCase(SedType.A011.toString());
        assertThat(a011.getNav().getBruker().getPerson().getFornavn()).isEqualToIgnoringCase("Testfornavn1");
        assertThat(a011.getMedlemskap()).isInstanceOf(MedlemskapA011.class);
        assertThat(a011.getMedlemskap()).isNotNull();
    }
}
