package no.nav.melosys.eessi.kafka.producers.mapping;


import no.nav.melosys.eessi.avro.MelosysEessiMelding;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010;
import no.nav.melosys.eessi.models.sed.nav.AapenPeriode;
import no.nav.melosys.eessi.models.sed.nav.MeldingOmLovvalg;
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010;
import no.nav.melosys.eessi.models.sed.nav.VedtakA010;
import org.junit.Test;
import static no.nav.melosys.eessi.kafka.producers.mapping.MelosysEessiMeldingMapperStubs.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MelosysEessiMeldingMapperA010Test {

    @Test
    public void mapA010_fastPeriode_verifiserPeriode() {
        MelosysEessiMeldingMapper mapper = new MelosysEessiMeldingMapperA010();

        SED sed = createSed(hentMedlemskap(true));
        sed.setSed("A010");
        MelosysEessiMelding melding = mapper.map("aktørid", sed, creteSedHendelse(),createSakInformasjon());

        assertThat(melding).isNotNull();
        assertThat(melding.getGsakSaksnummer()).isNotNull();
        assertThat(melding.getArtikkel()).isEqualTo("11_4");
        assertThat(melding.getPeriode().getTom()).isNotNull();
        assertThat(melding.getStatsborgerskap()).isNotEmpty();
        assertThat(melding.getJournalpostId()).isEqualTo("journalpost");
        assertThat(melding.getAktoerId()).isEqualTo("aktørid");
        assertThat(melding.getRinaSaksnummer()).isEqualTo("rinasak");
        assertThat(melding.getDokumentId()).isEqualTo("dokument");
        assertThat(melding.getLovvalgsland()).isEqualTo("SE");
        assertThat(melding.getGsakSaksnummer()).isEqualTo(123L);
    }

    @Test
    public void mapA010_aapenPeriode_verifiserPeriode() {
        MelosysEessiMeldingMapper mapper = new MelosysEessiMeldingMapperA010();

        SED sed = createSed(hentMedlemskap(false));
        sed.setSed("A009");
        MelosysEessiMelding melding = mapper.map("aktørid", sed, creteSedHendelse(),createSakInformasjon());

        assertThat(melding).isNotNull();
        assertThat(melding.getGsakSaksnummer()).isNotNull();
        assertThat(melding.getArtikkel()).isEqualTo("11_4");
        assertThat(melding.getPeriode().getTom()).isNull();
        assertThat(melding.getStatsborgerskap()).isNotEmpty();
        assertThat(melding.getJournalpostId()).isEqualTo("journalpost");
        assertThat(melding.getAktoerId()).isEqualTo("aktørid");
        assertThat(melding.getRinaSaksnummer()).isEqualTo("rinasak");
        assertThat(melding.getDokumentId()).isEqualTo("dokument");
        assertThat(melding.getLovvalgsland()).isEqualTo("SE");
        assertThat(melding.getGsakSaksnummer()).isEqualTo(123L);
    }

    private MedlemskapA010 hentMedlemskap(boolean fastperiode) {
        MedlemskapA010 medlemskapA010 = new MedlemskapA010();

        VedtakA010 vedtak = new VedtakA010();
        medlemskapA010.setVedtak(vedtak);

        PeriodeA010 periode = new PeriodeA010();
        if (fastperiode) {
            periode.setSluttdato("slutt");
            periode.setStartdato("start");
        } else {
            periode.setAapenperiode(new AapenPeriode());
            periode.getAapenperiode().setStartdato("aapen");
        }
        vedtak.setGjelderperiode(periode);

        vedtak.setLand("SE");
        vedtak.setEropprinneligvedtak("ja");

        medlemskapA010.setMeldingomlovvalg(new MeldingOmLovvalg());
        medlemskapA010.getMeldingomlovvalg().setArtikkel("11_4");

        return medlemskapA010;
    }
}