package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.io.IOException;
import java.net.URISyntaxException;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;


public class A010MapperTest {

    private A010Mapper a010Mapper = new A010Mapper();

    private SedDataDto sedData;
    private Lovvalgsperiode lovvalgsperiode;

    @Before
    public void setup() throws IOException, URISyntaxException {
        sedData = SedDataStub.getStub();
        lovvalgsperiode = sedData.getLovvalgsperioder().get(0);
        lovvalgsperiode.setLovvalgsland("NO");
    }

    @Test
    public void mapTilSed_medTilleggsbestemmelse_bestemmelseErLovligBlirMappetTilSed() {
        lovvalgsperiode.setBestemmelse(Bestemmelse.ART_11_3_b);
        lovvalgsperiode.setTilleggsBestemmelse(Bestemmelse.ART_11_3_c);

        SED sed = a010Mapper.mapTilSed(sedData);

        assertThat(sed).isNotNull();
        assertThat(sed.getSedType()).isEqualTo(SedType.A010.name());
        assertThat(sed.getMedlemskap()).isInstanceOf(MedlemskapA010.class);
        assertThat(sed.getNav().getArbeidsgiver()).allMatch(a -> "NO".equals(a.getAdresse().getLand()));

        MedlemskapA010 medlemskap = (MedlemskapA010) sed.getMedlemskap();

        assertThat(medlemskap.getMeldingomlovvalg().getArtikkel()).isEqualTo(lovvalgsperiode.getBestemmelse().getValue());
        assertThat(medlemskap.getVedtak().getGjelderperiode().getStartdato()).isNotNull();
        assertThat(medlemskap.getVedtak().getGjelderperiode().getSluttdato()).isNotNull();
        assertThat(medlemskap.getAndreland().getArbeidsgiver()).noneMatch(a -> "NO".equals(a.getAdresse().getLand()));
    }

    @Test
    public void mapTilSed_medTilleggsbestemmelseBestemmelseIkkeGyld_tilleggsBestemmelseBrukes() {
        lovvalgsperiode.setBestemmelse(Bestemmelse.ART_11_3_a);
        lovvalgsperiode.setTilleggsBestemmelse(Bestemmelse.ART_11_3_b);

        SED sed = a010Mapper.mapTilSed(sedData);

        assertThat(sed.getMedlemskap()).isInstanceOf(MedlemskapA010.class);

        MedlemskapA010 medlemskap = (MedlemskapA010) sed.getMedlemskap();
        assertThat(medlemskap.getMeldingomlovvalg().getArtikkel()).isEqualTo(lovvalgsperiode.getTilleggsBestemmelse().getValue());
        assertThat(medlemskap.getVedtak().getGjelderperiode().getStartdato()).isNotNull();
        assertThat(medlemskap.getVedtak().getGjelderperiode().getSluttdato()).isNotNull();
    }

    @Test
    public void mapTilSed_medTilleggsbestemmelse_bestemmelseOgTilleggsbestemmelseErUlovligKasterException() {
        final Bestemmelse bestemmelse = Bestemmelse.ART_11_3_a;
        lovvalgsperiode.setBestemmelse(bestemmelse);
        lovvalgsperiode.setTilleggsBestemmelse(Bestemmelse.ART_12_1);

        assertThatExceptionOfType(MappingException.class).isThrownBy(() -> a010Mapper.mapTilSed(sedData))
                .withMessageContaining("Kan ikke mappe til bestemmelse i A010 for lovvalgsperiode ");
    }
}
