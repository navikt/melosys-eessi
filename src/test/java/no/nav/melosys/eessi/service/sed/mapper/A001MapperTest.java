package no.nav.melosys.eessi.service.sed.mapper;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA001;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
public class A001MapperTest {

    private A001Mapper a001Mapper = new A001Mapper();

    private SedDataDto sedData;

    @Before
    public void setup() throws IOException, URISyntaxException {
        sedData = SedDataStub.getStub();

        Lovvalgsperiode lovvalgsperiode = new Lovvalgsperiode();
        lovvalgsperiode.setBestemmelse(Bestemmelse.ART_16_1);
        lovvalgsperiode.setFom(LocalDate.now());
        lovvalgsperiode.setTom(LocalDate.now().plusYears(1L));
        lovvalgsperiode.setLandkode("NO");
        sedData.setLovvalgsperioder(Collections.singletonList(lovvalgsperiode));

        sedData.setEgenAnsatt(false);
    }

    @Test
    public void mapTilSed() throws MappingException {
        SED sed = a001Mapper.mapTilSed(sedData);

        Assert.assertEquals(MedlemskapA001.class, sed.getMedlemskap().getClass());
    }
}

