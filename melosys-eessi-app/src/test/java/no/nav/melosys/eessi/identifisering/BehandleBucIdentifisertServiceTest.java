package no.nav.melosys.eessi.identifisering;

import java.util.List;
import java.util.Optional;

import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiAivenProducer;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Nav;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.DefaultMapper;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BehandleBucIdentifisertServiceTest {

    private final String RINA_SAKSNUMMER = "123456";
    private final String RINA_DOKUMENT_ID = "7890";
    private final String AKTOER_ID = "1122334455660";
    private final String FNR = "11223344550";
    private final String JOURNALPOST_ID = "11";
    private final String JOURNALPOST_ID_2 = "22";
    private final String JOURNALPOST_ID_3 = "33";

    @Mock
    private SedMottattHendelseRepository sedMottattHendelseRepository;
    @Mock
    private SaksrelasjonService saksrelasjonService;
    @Mock
    private EuxService euxService;
    @Mock
    private OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;
    @Mock
    private PersonFasade personFasade;
    @Mock
    private MelosysEessiMeldingMapperFactory melosysEessiMeldingMapperFactory;
    @Mock
    private MelosysEessiAivenProducer melosysEessiAivenProducer;

    private BehandleBucIdentifisertService behandleBucIdentifisertService;

    @BeforeEach
    void setup() {
        behandleBucIdentifisertService = new BehandleBucIdentifisertService(sedMottattHendelseRepository, saksrelasjonService, euxService, opprettInngaaendeJournalpostService, personFasade, melosysEessiMeldingMapperFactory, melosysEessiAivenProducer);
    }

    @Test
    void bucIdentifisert_3SEDer_oppretter2JournalposterOgPubliserer3Meldinger() {
        // Første SED som må identifiseres journalføres sammen med opprettelse av oppgave til ID fordeling
        var sedAlleredeJournalført = lagSedMottattHendelse("1", RINA_SAKSNUMMER, RINA_DOKUMENT_ID, JOURNALPOST_ID, false);
        // De påfølgende SEDene journalføres først når BUC blir identifisert
        var sed2 = lagSedMottattHendelse("2", RINA_SAKSNUMMER, RINA_DOKUMENT_ID, null, false);
        var sed3 = lagSedMottattHendelse("3", RINA_SAKSNUMMER, RINA_DOKUMENT_ID, null, false);

        when(sedMottattHendelseRepository.findAllByRinaSaksnummerAndPublisertKafkaSortedByMottattDato(RINA_SAKSNUMMER, false))
            .thenReturn(List.of(sedAlleredeJournalført, sed2, sed3));
        when(melosysEessiMeldingMapperFactory.getMapper(any())).thenReturn(new DefaultMapper());
        when(personFasade.hentAktoerId(FNR)).thenReturn(AKTOER_ID);
        when(euxService.hentSedMedVedlegg(RINA_SAKSNUMMER, RINA_DOKUMENT_ID)).thenReturn(new SedMedVedlegg(null, null));
        when(euxService.hentSed(RINA_SAKSNUMMER, RINA_DOKUMENT_ID)).thenReturn(lagSED());
        when(euxService.sedErEndring(RINA_DOKUMENT_ID, RINA_SAKSNUMMER)).thenReturn(false);
        when(saksrelasjonService.finnVedRinaSaksnummer(RINA_SAKSNUMMER)).thenReturn(Optional.empty());
        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(eq(sed2.getSedHendelse()), any(), eq(FNR))).thenReturn(JOURNALPOST_ID_2);
        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(eq(sed3.getSedHendelse()), any(), eq(FNR))).thenReturn(JOURNALPOST_ID_3);


        behandleBucIdentifisertService.bucIdentifisert(RINA_SAKSNUMMER, FNR);


        verify(sedMottattHendelseRepository, times(2)).save(any());
        verify(melosysEessiAivenProducer, times(3)).publiserMelding(any());

        assertThat(sedAlleredeJournalført.isPublisertKafka()).isTrue();
        assertThat(sedAlleredeJournalført.getJournalpostId()).isEqualTo(JOURNALPOST_ID);

        assertThat(sed2.isPublisertKafka()).isTrue();
        assertThat(sed2.getJournalpostId()).isEqualTo(JOURNALPOST_ID_2);

        assertThat(sed3.isPublisertKafka()).isTrue();
        assertThat(sed3.getJournalpostId()).isEqualTo(JOURNALPOST_ID_3);
    }

    @Test
    void bucIdentifisert_SEDErX100_ignoreres() {
        // Første SED som må identifiseres journalføres sammen med opprettelse av oppgave til ID fordeling
        var sedAlleredeJournalført = lagSedMottattHendelse("1", RINA_SAKSNUMMER, RINA_DOKUMENT_ID, JOURNALPOST_ID, false);
        var sedSomSkalIgnoreres = lagSedMottattHendelse("2", RINA_SAKSNUMMER, RINA_DOKUMENT_ID, null, false);
        sedSomSkalIgnoreres.getSedHendelse().setSedType(SedType.X100.name());

        when(sedMottattHendelseRepository.findAllByRinaSaksnummerAndPublisertKafkaSortedByMottattDato(RINA_SAKSNUMMER, false))
            .thenReturn(List.of(sedAlleredeJournalført, sedSomSkalIgnoreres));
        when(melosysEessiMeldingMapperFactory.getMapper(any())).thenReturn(new DefaultMapper());
        when(personFasade.hentAktoerId(FNR)).thenReturn(AKTOER_ID);
        when(euxService.hentSed(RINA_SAKSNUMMER, RINA_DOKUMENT_ID)).thenReturn(lagSED());
        when(euxService.sedErEndring(RINA_DOKUMENT_ID, RINA_SAKSNUMMER)).thenReturn(false);
        when(saksrelasjonService.finnVedRinaSaksnummer(RINA_SAKSNUMMER)).thenReturn(Optional.empty());


        behandleBucIdentifisertService.bucIdentifisert(RINA_SAKSNUMMER, FNR);


        verify(sedMottattHendelseRepository, never()).save(any());
        verify(melosysEessiAivenProducer).publiserMelding(any());

        assertThat(sedAlleredeJournalført.isPublisertKafka()).isTrue();
        assertThat(sedAlleredeJournalført.getJournalpostId()).isEqualTo(JOURNALPOST_ID);

        assertThat(sedSomSkalIgnoreres.isPublisertKafka()).isFalse();
    }


    private SedMottattHendelse lagSedMottattHendelse(String sedID, String rinaSaksnummer, String rinaDokumentID, String journalpostID, boolean publisertKafka) {
        return SedMottattHendelse.builder()
            .sedHendelse(SedHendelse.builder()
                .sedId(sedID)
                .rinaSakId(rinaSaksnummer)
                .rinaDokumentId(rinaDokumentID)
                .sedType(SedType.A003.name())
                .avsenderId("DK:88855")
                .build())
            .journalpostId(journalpostID)
            .publisertKafka(publisertKafka)
            .build();
    }

    private SED lagSED() {
        var sed = new SED();
        sed.setNav(new Nav());
        return sed;
    }
}
