package no.nav.melosys.eessi.service.sed.mapper.lovvalg;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.controller.dto.Periode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.SvarAnmodningUnntakDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA002;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class A002MapperTest {
    private static final String BEGRUNNELSE = "begrunnelse fritekst";

    private A002Mapper a002Mapper = new A002Mapper();

    private SED a001;

    @Before
    public void setup() throws IOException {
        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA001.json");
        a001 = new ObjectMapper().readValue(jsonUrl, SED.class);
    }

    @Test
    public void mapFraEksisterendeSedA001() {
        LocalDate fom = LocalDate.now();
        LocalDate tom = LocalDate.now().plusDays(2L);
        SED a002 = a002Mapper.mapFraSed(a001, BEGRUNNELSE, SvarAnmodningUnntakBeslutning.DELVIS_INNVILGELSE, fom, tom);

        assertThat(a002.getSed()).isEqualToIgnoringCase(SedType.A002.toString());
        assertThat(a002.getNav().getBruker().getPerson().getFornavn()).isEqualToIgnoringCase("Testfornavn1");
        assertThat(a002.getMedlemskap()).isNotNull();

        assertThat(a002.getMedlemskap()).isInstanceOf(MedlemskapA002.class);
        MedlemskapA002 medlemskapA002 = (MedlemskapA002) a002.getMedlemskap();
        assertThat(medlemskapA002.getUnntak().getVedtak().getBegrunnelse()).isEqualTo(BEGRUNNELSE);
        assertThat(medlemskapA002.getUnntak().getVedtak().getResultat()).isEqualTo(SvarAnmodningUnntakBeslutning.DELVIS_INNVILGELSE.getRinaKode());
        assertThat(medlemskapA002.getUnntak().getVedtak().getAnnenperiode().getFastperiode().getStartdato()).isEqualTo(a002Mapper.formaterDato(fom));
        assertThat(medlemskapA002.getUnntak().getVedtak().getAnnenperiode().getFastperiode().getSluttdato()).isEqualTo(a002Mapper.formaterDato(tom));
    }

    @Test
    public void mapTilSed_forventSed() throws IOException, URISyntaxException, MappingException, NotFoundException {
        SedDataDto sedData = SedDataStub.getStub();
        SvarAnmodningUnntakDto svarAnmodningUnntakDto = new SvarAnmodningUnntakDto(
                SvarAnmodningUnntakBeslutning.AVSLAG,
                "begrunnelse",
                new Periode(LocalDate.now(), LocalDate.now().plusDays(1L))
        );
        sedData.setSvarAnmodningUnntak(svarAnmodningUnntakDto);

        SED a002 = a002Mapper.mapTilSed(sedData);
        assertThat(a002).isNotNull();
        assertThat(a002.getMedlemskap()).isInstanceOf(MedlemskapA002.class);
    }

    @Test(expected = MappingException.class)
    public void mapTilSed_utenSvarAnmodningUnntak_forventException() throws MappingException, NotFoundException, IOException, URISyntaxException {
        SedDataDto sedData = SedDataStub.getStub();
        a002Mapper.mapTilSed(sedData);
    }
}
