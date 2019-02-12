package no.nav.melosys.eessi.service.sed.mapper;

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

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collections;

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
        lovvalgsperiode.setLandkode("NOR");
        sedData.setLovvalgsperioder(Collections.singletonList(lovvalgsperiode));

        sedData.setEgenAnsatt(false);
    }

    @Test
    public void hentMedlemskapIkkeSelvstendigOg12_1_forventGyldigMedlemskap() throws MappingException, NotFoundException {
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
    public void hentMedlemskapErSelvstendigOg12_2_forventGyldigMedlemskap() throws MappingException, NotFoundException {
        sedData.getLovvalgsperioder().get(0).setBestemmelse(Bestemmelse.ART_12_2);
        sedData.setEgenAnsatt(true);
        SED sed = a009Mapper.mapTilSed(sedData);

        assertEquals(MedlemskapA009.class, sed.getMedlemskap().getClass());

        MedlemskapA009 medlemskapA009 = (MedlemskapA009) sed.getMedlemskap();

        assertNotNull(medlemskapA009);
        assertNull(medlemskapA009.getUtsendingsland());

        assertNotNull(medlemskapA009.getVedtak());
        assertEquals("12_2", medlemskapA009.getVedtak().getArtikkelforordning());
        assertNotNull(medlemskapA009.getVedtak().getGjelderperiode().getFastperiode());
        assertNull(medlemskapA009.getVedtak().getGjelderperiode().getAapenperiode());
    }

    @Test(expected = MappingException.class)
    public void hentMedlemkapFeilLovvalgsBestemmelse_forventFunksjonellException() throws MappingException, NotFoundException {
        sedData.getLovvalgsperioder().get(0).setBestemmelse(Bestemmelse.ART_13_4);
        a009Mapper.mapTilSed(sedData);
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("unchecked")
    public void ingenLovvalgsperioder_forventTekniskException() throws MappingException, NotFoundException {
        sedData.setLovvalgsperioder(null);
        a009Mapper.mapTilSed(sedData);
    }
}