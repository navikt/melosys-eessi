package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collections;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class A009MapperTest {

    private A009Mapper a009Mapper = new A009Mapper();

    private SedDataDto sedData;

    @Before
    public void setup() throws IOException, URISyntaxException {
        sedData = SedDataStub.getStub();

        Lovvalgsperiode lovvalgsperiode = new Lovvalgsperiode();
        lovvalgsperiode.setBestemmelse(Bestemmelse.ART_12_1);
        lovvalgsperiode.setFom(LocalDate.now());
        lovvalgsperiode.setTom(LocalDate.now().plusYears(1L));
        lovvalgsperiode.setLovvalgsland("NOR");
        sedData.setLovvalgsperioder(Collections.singletonList(lovvalgsperiode));
    }

    @Test
    public void getMedlemskapIkkeSelvstendigOg12_1_expectGyldigMedlemskap() throws MappingException, NotFoundException {
        SED sed = a009Mapper.mapTilSed(sedData);

        assertEquals(MedlemskapA009.class, sed.getMedlemskap().getClass());

        MedlemskapA009 medlemskapA009 = (MedlemskapA009) sed.getMedlemskap();

        assertNotNull(medlemskapA009);
        assertNotNull(medlemskapA009.getUtsendingsland());
        assertEquals(sed.getNav().getArbeidsgiver().get(0).getNavn(),
                medlemskapA009.getUtsendingsland().getArbeidsgiver().get(0).getNavn());

        assertNotNull(medlemskapA009.getVedtak());
        assertEquals("12_1", medlemskapA009.getVedtak().getArtikkelforordning());
        assertNotNull(medlemskapA009.getVedtak().getGjelderperiode().getFastperiode());
        assertNull(medlemskapA009.getVedtak().getGjelderperiode().getAapenperiode());
    }

    @Test
    public void getMedlemskapErSelvstendigOg12_2_expectGyldigMedlemskap() throws MappingException, NotFoundException {
        sedData.getLovvalgsperioder().get(0).setBestemmelse(Bestemmelse.ART_12_2);
        SED sed = a009Mapper.mapTilSed(sedData);

        assertEquals(MedlemskapA009.class, sed.getMedlemskap().getClass());

        MedlemskapA009 medlemskapA009 = (MedlemskapA009) sed.getMedlemskap();

        assertNotNull(medlemskapA009);

        assertNotNull(medlemskapA009.getVedtak());
        assertEquals("12_2", medlemskapA009.getVedtak().getArtikkelforordning());
        assertNotNull(medlemskapA009.getVedtak().getGjelderperiode().getFastperiode());
        assertNull(medlemskapA009.getVedtak().getGjelderperiode().getAapenperiode());
    }

    @Test(expected = MappingException.class)
    public void getMedlemskapFeilLovvalgsBestemmelse_expectMappingException() throws MappingException, NotFoundException {
        sedData.getLovvalgsperioder().get(0).setBestemmelse(Bestemmelse.ART_13_4);
        a009Mapper.mapTilSed(sedData);
    }

    @Test(expected = NullPointerException.class)
    public void ingenLovvalgsperioder_expectNullPointerException() throws MappingException, NotFoundException {
        sedData.setLovvalgsperioder(null);
        a009Mapper.mapTilSed(sedData);
    }
}