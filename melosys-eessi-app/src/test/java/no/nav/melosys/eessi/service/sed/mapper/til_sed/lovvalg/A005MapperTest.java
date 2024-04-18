package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.io.IOException;
import java.net.URISyntaxException;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA005;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class A005MapperTest {

    private final A005Mapper sedMapper = new A005Mapper();

    @Test
    void mapTilSed() throws IOException, URISyntaxException, MappingException, NotFoundException {
        SedDataDto sedDataDto = SedDataStub.getStub();
        SED sed = sedMapper.mapTilSed(sedDataDto, false);

        assertThat(sed).isNotNull();
        assertThat(sed.getMedlemskap()).isInstanceOf(MedlemskapA005.class);
        assertThat(sed.getNav().getArbeidsland()).isNull();
        assertThat(sed.getSedVer()).isEqualTo("2");
        assertThat(sed.getSedGVer()).isEqualTo("4");
    }


    @Test
    void mapTilSed4_3_skalIkkeBliPåvirketAvToggleCDM4_3() throws IOException, URISyntaxException, MappingException, NotFoundException {
        SedDataDto sedDataDto = SedDataStub.getStub();
        SED sed = sedMapper.mapTilSed(sedDataDto, true);

        assertThat(sed).isNotNull();
        assertThat(sed.getMedlemskap()).isInstanceOf(MedlemskapA005.class);
        assertThat(sed.getNav().getArbeidsland()).isNull();
        assertThat(sed.getSedVer()).isEqualTo("2");
        assertThat(sed.getSedGVer()).isEqualTo("4");

    }
}
