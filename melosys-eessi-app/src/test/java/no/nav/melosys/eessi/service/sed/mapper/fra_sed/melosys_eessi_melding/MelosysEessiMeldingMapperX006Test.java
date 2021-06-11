package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Institusjon;
import no.nav.melosys.eessi.models.sed.nav.Sak;
import no.nav.melosys.eessi.models.sed.nav.X006FjernInstitusjon;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MelosysEessiMeldingMapperX006Test {

    private SedHendelse sedHendelse;
    private SakInformasjon sakInformasjon;
    private MelosysEessiMeldingMapperFactory melosysEessiMeldingMapperFactory;

    private static final String NORSK_INSTITUSJONS_ID = "NO:NAVAT07";

    @BeforeEach
    public void setup() {
        sedHendelse = createSedHendelse();
        sakInformasjon = createSakInformasjon();
        melosysEessiMeldingMapperFactory = new MelosysEessiMeldingMapperFactory(NORSK_INSTITUSJONS_ID);
    }

    @Test
    public void mapX006_norskInsitutsjonErMottaker_oppdatererMelosysEessiMeldingOpprettetMedInstitusjonFlaggSatt() {
        SED sed = createSed(null);
        lagNavSak(sed);

        Institusjon institusjon = lagInstitusjon(NORSK_INSTITUSJONS_ID, "NO:NAVAT07 Norge nav");

        X006FjernInstitusjon fjernInstitusjon = new X006FjernInstitusjon();
        fjernInstitusjon.setInstitusjon(institusjon);
        sed.getNav().getSak().setFjerninstitusjon(fjernInstitusjon);
        MelosysEessiMelding melosysEessiMelding = melosysEessiMeldingMapperFactory.getMapper(SedType.X006)
                .map("123", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                        sedHendelse.getSedType(), sedHendelse.getBucType(), sedHendelse.getAvsenderId(), "landkode", sakInformasjon.getJournalpostId(),
                        sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), false, "1"
                );

        assertThat(melosysEessiMelding).isNotNull();
        assertThat(melosysEessiMelding.isErMottaksInstitusjon()).isTrue();
    }

    @Test
    public void mapX006_norskInsitutsjonErIkkeMottaker_oppdatererMelosysEessiMeldingOpprettetMedInstitusjonFlaggIkkeSatt() {
        SED sed = createSed(null);
        lagNavSak(sed);
        Institusjon institusjon = lagInstitusjon("DE:DENMARK09", "DE:DENMARK09 Danmark");

        X006FjernInstitusjon fjernInstitusjon = new X006FjernInstitusjon();
        fjernInstitusjon.setInstitusjon(institusjon);

        sed.getNav().getSak().setFjerninstitusjon(fjernInstitusjon);
        MelosysEessiMelding melosysEessiMelding = melosysEessiMeldingMapperFactory.getMapper(SedType.X006)
                .map("123", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                        sedHendelse.getSedType(), sedHendelse.getBucType(), sedHendelse.getAvsenderId(), "landkode", sakInformasjon.getJournalpostId(),
                        sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), false, "1"
                );

        assertThat(melosysEessiMelding).isNotNull();
        assertThat(melosysEessiMelding.isErMottaksInstitusjon()).isFalse();
    }

    @Test
    public void mapX006_institusjonIDSattNull_oppdatererMelosysEessiMeldingOpprettetMedInstitusjonFlaggIkkeSatt() {
        SED sed = createSed(null);
        lagNavSak(sed);
        Institusjon institusjon = lagInstitusjon(null, null);

        X006FjernInstitusjon fjernInstitusjon = new X006FjernInstitusjon();
        fjernInstitusjon.setInstitusjon(institusjon);

        sed.getNav().getSak().setFjerninstitusjon(fjernInstitusjon);
        MelosysEessiMelding melosysEessiMelding = melosysEessiMeldingMapperFactory.getMapper(SedType.X006)
                .map("123", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                        sedHendelse.getSedType(), sedHendelse.getBucType(), sedHendelse.getAvsenderId(), "landkode", sakInformasjon.getJournalpostId(),
                        sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), false, "1"
                );

        assertThat(melosysEessiMelding).isNotNull();
        assertThat(melosysEessiMelding.isErMottaksInstitusjon()).isFalse();
    }

    @Test
    public void mapX006_institusjonIDSattTom_oppdatererMelosysEessiMeldingOpprettetMedInstitusjonFlaggIkkeSatt() {
        SED sed = createSed(null);
        lagNavSak(sed);
        Institusjon institusjon = lagInstitusjon("", "");

        X006FjernInstitusjon fjernInstitusjon = new X006FjernInstitusjon();
        fjernInstitusjon.setInstitusjon(institusjon);

        sed.getNav().getSak().setFjerninstitusjon(fjernInstitusjon);
        MelosysEessiMelding melosysEessiMelding = melosysEessiMeldingMapperFactory.getMapper(SedType.X006)
                .map("123", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                        sedHendelse.getSedType(), sedHendelse.getBucType(), sedHendelse.getAvsenderId(), "landkode", sakInformasjon.getJournalpostId(),
                        sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), false, "1"
                );

        assertThat(melosysEessiMelding).isNotNull();
        assertThat(melosysEessiMelding.isErMottaksInstitusjon()).isFalse();
    }

    private Institusjon lagInstitusjon(String id, String navn) {
        Institusjon institusjon = new Institusjon();
        institusjon.setId(id);
        institusjon.setNavn(navn);
        return institusjon;
    }

    private void lagNavSak(SED sed) {
        if (sed.getNav() != null) {
            sed.getNav().setSak(new Sak());
        }
    }
}