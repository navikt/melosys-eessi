package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.io.IOException;
import java.net.URISyntaxException;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA008;
import no.nav.melosys.eessi.models.sed.nav.ArbeidIFlereLand;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class A008MapperTest {

    private final A008Mapper a008Mapper = new A008Mapper();

    private SedDataDto sedData;

    @BeforeEach
    public void setup() throws IOException, URISyntaxException {
        sedData = SedDataStub.getStub();
    }

    @Test
    void mapTilSed() throws MappingException, NotFoundException {
        sedData.setAvklartBostedsland("SE");
        SED sed = a008Mapper.mapTilSed(sedData, false);
        assertThat(sed.getMedlemskap()).isInstanceOf(MedlemskapA008.class);

        MedlemskapA008 medlemskap = (MedlemskapA008) sed.getMedlemskap();
        ArbeidIFlereLand arbeidIFlereLand = medlemskap.getBruker().getArbeidiflereland();
        assertThat(arbeidIFlereLand.getYrkesaktivitet().getStartdato()).isEqualTo("2020-01-01");
        assertThat(arbeidIFlereLand.getBosted().getLand()).isEqualTo("SE");
        assertThat(sed.getNav().getArbeidsland()).isNull();
        assertThat(sed.getSedVer()).isEqualTo("2");
        assertThat(sed.getSedGVer()).isEqualTo("4");
    }

    @Test
    void mapTilSed4_3() throws MappingException, NotFoundException {
        sedData.setAvklartBostedsland("SE");
        SED sed = a008Mapper.mapTilSed(sedData, true);
        assertThat(sed.getMedlemskap()).isInstanceOf(MedlemskapA008.class);

        MedlemskapA008 medlemskap = (MedlemskapA008) sed.getMedlemskap();
        ArbeidIFlereLand arbeidIFlereLand = medlemskap.getBruker().getArbeidiflereland();
        assertThat(arbeidIFlereLand.getYrkesaktivitet().getStartdato()).isEqualTo("2020-01-01");
        assertThat(arbeidIFlereLand.getBosted().getLand()).isEqualTo("SE");
        assertThat(sed.getNav().getArbeidsland()).hasSize(1);
        assertThat(sed.getSedVer()).isEqualTo("3");
        assertThat(sed.getSedGVer()).isEqualTo("4");
    }

}
