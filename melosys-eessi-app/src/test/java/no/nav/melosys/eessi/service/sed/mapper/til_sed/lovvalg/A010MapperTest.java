package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.VedtakDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;


class A010MapperTest {

    private final A010Mapper a010Mapper = new A010Mapper();

    private SedDataDto sedData;
    private Lovvalgsperiode lovvalgsperiode;

    @BeforeEach
    public void setup() throws IOException, URISyntaxException {
        sedData = SedDataStub.getStub();
        lovvalgsperiode = sedData.getLovvalgsperioder().get(0);
        lovvalgsperiode.setLovvalgsland("NO");
    }

    @Test
    void mapTilSed() throws MappingException, NotFoundException {
        lovvalgsperiode.setBestemmelse(Bestemmelse.ART_11_3_b);
        lovvalgsperiode.setTilleggsBestemmelse(Bestemmelse.ART_11_3_c);

        sedData.setAvklartBostedsland("SE");
        SED sed = a010Mapper.mapTilSed(sedData, false);
        assertThat(sed.getMedlemskap()).isInstanceOf(MedlemskapA010.class);

        assertThat(sed.getNav().getArbeidsland()).isNull();
        assertThat(sed.getSedVer()).isEqualTo("2");
        assertThat(sed.getSedGVer()).isEqualTo("4");
    }

    @Test
    void mapTilSed4_3() throws MappingException, NotFoundException {
        lovvalgsperiode.setBestemmelse(Bestemmelse.ART_11_3_b);
        lovvalgsperiode.setTilleggsBestemmelse(Bestemmelse.ART_11_3_c);

        sedData.setAvklartBostedsland("SE");
        SED sed = a010Mapper.mapTilSed(sedData, true);
        assertThat(sed.getMedlemskap()).isInstanceOf(MedlemskapA010.class);

        assertThat(sed.getNav().getArbeidsland()).hasSize(1);
        assertThat(sed.getSedVer()).isEqualTo("3");
        assertThat(sed.getSedGVer()).isEqualTo("4");
    }

    @Test
    void mapTilSed_medTilleggsbestemmelse_bestemmelseErLovligBlirMappetTilSed() {
        lovvalgsperiode.setBestemmelse(Bestemmelse.ART_11_3_b);
        lovvalgsperiode.setTilleggsBestemmelse(Bestemmelse.ART_11_3_c);

        SED sed = a010Mapper.mapTilSed(sedData, false);

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
    void mapTilSed_medTilleggsbestemmelseBestemmelseIkkeGyld_tilleggsBestemmelseBrukes() {
        lovvalgsperiode.setBestemmelse(Bestemmelse.ART_11_3_a);
        lovvalgsperiode.setTilleggsBestemmelse(Bestemmelse.ART_11_3_b);

        SED sed = a010Mapper.mapTilSed(sedData, false);

        assertThat(sed.getMedlemskap()).isInstanceOf(MedlemskapA010.class);

        MedlemskapA010 medlemskap = (MedlemskapA010) sed.getMedlemskap();
        assertThat(medlemskap.getMeldingomlovvalg().getArtikkel()).isEqualTo(lovvalgsperiode.getTilleggsBestemmelse().getValue());
        assertThat(medlemskap.getVedtak().getGjelderperiode().getStartdato()).isNotNull();
        assertThat(medlemskap.getVedtak().getGjelderperiode().getSluttdato()).isNotNull();
    }

    @Test
    void mapTilSed_erIkkeOpprinneligVedtak_ErOpprinneligVedtaksOgErEndringsvedtakSattKorrektOgDatoForrigeVedtakIkkeNull() {
        lovvalgsperiode.setBestemmelse(Bestemmelse.ART_11_3_a);
        lovvalgsperiode.setTilleggsBestemmelse(Bestemmelse.ART_11_3_b);
        VedtakDto vedtakDto = new VedtakDto();
        vedtakDto.setErFørstegangsvedtak(false);
        vedtakDto.setDatoForrigeVedtak(LocalDate.now());
        sedData.setVedtakDto(vedtakDto);


        SED sed = a010Mapper.mapTilSed(sedData, false);


        assertThat(sed.getMedlemskap().getClass()).isEqualTo(MedlemskapA010.class);
        MedlemskapA010 medlemskapA010 = (MedlemskapA010) sed.getMedlemskap();
        assertThat(medlemskapA010).isNotNull();
        assertThat(medlemskapA010.getVedtak().getEropprinneligvedtak()).isNull();
        assertThat(medlemskapA010.getVedtak().getErendringsvedtak()).isEqualTo("nei");
        assertThat(medlemskapA010.getVedtak().getDatoforrigevedtak()).isEqualTo(LocalDate.now().toString());
    }

    @Test
    void mapTilSed_erOpprinneligVedtak_ErOpprinneligVedtaksOgErEndringsvedtakSattKorrektOgDatoForrigeVedtakNull() {
        lovvalgsperiode.setBestemmelse(Bestemmelse.ART_11_3_a);
        lovvalgsperiode.setTilleggsBestemmelse(Bestemmelse.ART_11_3_b);
        VedtakDto vedtakDto = new VedtakDto();
        vedtakDto.setErFørstegangsvedtak(true);
        sedData.setVedtakDto(vedtakDto);


        SED sed = a010Mapper.mapTilSed(sedData, false);


        assertThat(sed.getMedlemskap().getClass()).isEqualTo(MedlemskapA010.class);
        MedlemskapA010 medlemskapA010 = (MedlemskapA010) sed.getMedlemskap();
        assertThat(medlemskapA010).isNotNull();
        assertThat(medlemskapA010.getVedtak().getEropprinneligvedtak()).isEqualTo("ja");
        assertThat(medlemskapA010.getVedtak().getErendringsvedtak()).isNull();
        assertThat(medlemskapA010.getVedtak().getDatoforrigevedtak()).isNull();
    }

    @Test
    void mapTilSed_medTilleggsbestemmelse_bestemmelseOgTilleggsbestemmelseErUlovligKasterException() {
        final Bestemmelse bestemmelse = Bestemmelse.ART_11_3_a;
        lovvalgsperiode.setBestemmelse(bestemmelse);
        lovvalgsperiode.setTilleggsBestemmelse(Bestemmelse.ART_12_1);

        assertThatExceptionOfType(MappingException.class).isThrownBy(() -> a010Mapper.mapTilSed(sedData, false))
            .withMessageContaining("Kan ikke mappe til bestemmelse i A010 for lovvalgsperiode ");
    }
}
