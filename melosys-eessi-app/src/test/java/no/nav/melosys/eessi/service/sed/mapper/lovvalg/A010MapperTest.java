package no.nav.melosys.eessi.service.sed.mapper.lovvalg;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class A010MapperTest {

    private A010Mapper a010Mapper = new A010Mapper();

    private SedDataDto sedData;

    @Before
    public void setup() throws IOException, URISyntaxException {
        sedData = SedDataStub.getStub();
        Lovvalgsperiode lovvalgsperiode = new Lovvalgsperiode();
        lovvalgsperiode.setBestemmelse(Bestemmelse.ART_11_3_e);
        lovvalgsperiode.setFom(LocalDate.now());
        lovvalgsperiode.setTom(LocalDate.now().plusYears(1L));
        lovvalgsperiode.setLovvalgsland("NOR");
        sedData.setLovvalgsperioder(Collections.singletonList(lovvalgsperiode));
    }

    @Test
    public void mapTilSed() throws MappingException, NotFoundException {
        SED sed = a010Mapper.mapTilSed(sedData);

        assertThat(sed).isNotNull();
        assertThat(sed.getSedType()).isEqualTo(SedType.A010.name());
        assertThat(sed.getMedlemskap()).isInstanceOf(MedlemskapA010.class);

        MedlemskapA010 medlemskap = (MedlemskapA010) sed.getMedlemskap();

        assertThat(medlemskap.getMeldingomlovvalg().getArtikkel()).isEqualTo(Bestemmelse.ART_11_3_e.getValue());
        assertThat(medlemskap.getVedtak().getGjelderperiode().getStartdato()).isNotNull();
        assertThat(medlemskap.getVedtak().getGjelderperiode().getSluttdato()).isNotNull();
    }
}
