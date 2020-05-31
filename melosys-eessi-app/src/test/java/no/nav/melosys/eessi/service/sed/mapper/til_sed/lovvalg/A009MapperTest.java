package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
public class A009MapperTest {

    private A009Mapper a009Mapper = new A009Mapper();

    private SedDataDto sedData;

    private final String lovvalgsland = "NO";

    @Before
    public void setup() throws IOException, URISyntaxException {
        sedData = SedDataStub.getStub();

        Lovvalgsperiode lovvalgsperiode = sedData.getLovvalgsperioder().get(0);
        lovvalgsperiode.setBestemmelse(Bestemmelse.ART_12_1);
        lovvalgsperiode.setFom(LocalDate.now());
        lovvalgsperiode.setTom(LocalDate.now().plusYears(1L));
        lovvalgsperiode.setLovvalgsland(lovvalgsland);
    }

    @Test
    public void getMedlemskapIkkeSelvstendigOg12_1_expectGyldigMedlemskap() {
        SED sed = a009Mapper.mapTilSed(sedData);

        assertThat(MedlemskapA009.class).isEqualTo(sed.getMedlemskap().getClass());

        MedlemskapA009 medlemskapA009 = (MedlemskapA009) sed.getMedlemskap();

        assertThat(medlemskapA009).isNotNull();
        assertThat(medlemskapA009.getUtsendingsland()).isNotNull();
        assertThat(medlemskapA009.getUtsendingsland().getArbeidsgiver()).allMatch(a -> a.getAdresse().getLand().equals(lovvalgsland));
        assertThat(medlemskapA009.getAndreland().getArbeidsgiver()).noneMatch(a -> a.getAdresse().getLand().equals(lovvalgsland));

        assertThat(medlemskapA009.getVedtak()).isNotNull();
        assertThat(medlemskapA009.getVedtak().getArtikkelforordning()).isEqualTo("12_1");
        assertThat(medlemskapA009.getVedtak().getGjelderperiode().getFastperiode()).isNotNull();
        assertThat(medlemskapA009.getVedtak().getGjelderperiode().getAapenperiode()).isNull();
    }

    @Test
    public void getMedlemskapErSelvstendigOg12_2_expectGyldigMedlemskap() {
        sedData.getLovvalgsperioder().get(0).setBestemmelse(Bestemmelse.ART_12_2);
        SED sed = a009Mapper.mapTilSed(sedData);

        assertThat(sed.getMedlemskap().getClass()).isEqualTo(MedlemskapA009.class);

        MedlemskapA009 medlemskapA009 = (MedlemskapA009) sed.getMedlemskap();

        assertThat(medlemskapA009).isNotNull();

        assertThat(medlemskapA009.getVedtak()).isNotNull();
        assertThat(medlemskapA009.getVedtak().getArtikkelforordning()).isEqualTo("12_2");
        assertThat(medlemskapA009.getVedtak().getGjelderperiode().getFastperiode()).isNotNull();
        assertThat(medlemskapA009.getVedtak().getGjelderperiode().getAapenperiode()).isNull();
    }

    @Test(expected = MappingException.class)
    public void getMedlemskapFeilLovvalgsBestemmelse_expectMappingException() {
        sedData.getLovvalgsperioder().get(0).setBestemmelse(Bestemmelse.ART_13_4);
        a009Mapper.mapTilSed(sedData);
    }

    @Test(expected = NullPointerException.class)
    public void ingenLovvalgsperioder_expectNullPointerException() {
        sedData.setLovvalgsperioder(null);
        a009Mapper.mapTilSed(sedData);
    }

    @Test
    public void erIkkeFysisk_forventErIkkeFastadresse() {
        sedData.getArbeidssteder().get(0).setFysisk(false);
        SED sed = a009Mapper.mapTilSed(sedData);
        assertThat(sed.getNav().getArbeidssted().get(0).getErikkefastadresse()).isEqualTo("ja");
    }
}
