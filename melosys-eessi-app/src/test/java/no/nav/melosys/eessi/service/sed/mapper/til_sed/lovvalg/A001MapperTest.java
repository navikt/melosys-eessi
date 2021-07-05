package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA001;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class A001MapperTest {

    private final A001Mapper a001Mapper = new A001Mapper();

    private SedDataDto sedData;

    @BeforeEach
    public void setup() throws IOException, URISyntaxException {
        sedData = SedDataStub.getStub();

        Lovvalgsperiode lovvalgsperiode = sedData.getLovvalgsperioder().get(0);
        lovvalgsperiode.setBestemmelse(Bestemmelse.ART_16_1);
        lovvalgsperiode.setFom(LocalDate.now());
        lovvalgsperiode.setTom(LocalDate.now().plusYears(1L));
        lovvalgsperiode.setLovvalgsland("NO");
        lovvalgsperiode.setUnntakFraLovvalgsland("SE");
        lovvalgsperiode.setUnntakFraBestemmelse(Bestemmelse.ART_16_1);
    }

    @Test
    void mapTilSed() throws MappingException, NotFoundException {
        SED sed = a001Mapper.mapTilSed(sedData);

        assertThat(sed.getMedlemskap()).isInstanceOf(MedlemskapA001.class);

        assertThat(sed.getMedlemskap()).isNotNull().isInstanceOf(MedlemskapA001.class);

        MedlemskapA001 medlemskapA001 = (MedlemskapA001) sed.getMedlemskap();
        assertThat(sed.getNav().getArbeidsgiver()).allMatch(a -> "NO".equalsIgnoreCase(a.getAdresse().getLand()));
        assertThat(medlemskapA001.getVertsland().getArbeidsgiver()).noneMatch(a -> "NO".equalsIgnoreCase(a.getAdresse().getLand()));
    }
}

