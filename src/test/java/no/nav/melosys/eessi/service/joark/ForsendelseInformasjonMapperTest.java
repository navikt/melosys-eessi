package no.nav.melosys.eessi.service.joark;

import java.time.ZonedDateTime;
import no.nav.dokarkivsed.api.v1.*;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ForsendelseInformasjonMapperTest {

    private ForsendelseInformasjonMapper mapper = new ForsendelseInformasjonMapper();

    @Test
    public void createForsendelse_expectValidForsendelsesInformasjon() {
        ForsendelsesInformasjon res = mapper.createForsendelse("aktoerid", getSedHendelseStub(), getSakStub(),getReceiverInfoStub());
        assertThat(res, not(nullValue()));
        assertThat(res.getBruker(), not(nullValue()));
        assertThat(res.getBruker(), instanceOf(Person.class));
        assertThat(res.getMottaker(), instanceOf(Organisasjon.class));
        assertThat(res.getTema(), is("MED"));
        assertThat(res.getBucId(), is("11111"));
    }

    @Test
    public void createForsendelse_expectIkkeTilgjengligMottakerAndNoArkivsakAndNoBruker() {
        ForsendelsesInformasjon res = mapper.createForsendelse("", getSedHendelseStub(), null, null);
        assertThat(res.getMottaker(), not(nullValue()));
        assertThat(((Organisasjon) res.getMottaker()).getNavn(), is("Ikke tilgjengelig"));
        assertThat(((Organisasjon) res.getMottaker()).getOrgnummer(), is("Ikke tilgjengelig"));
        assertThat(res.getBruker(), is(nullValue()));
        assertThat(res.getArkivSak(), is(nullValue()));
    }

    @Test
    public void createHoveddokument_expectValidInformation() {
        DokumentInfoHoveddokument hoveddokument = mapper.hoveddokument("LA_BUC_04", new byte[0]);
        assertThat(hoveddokument.getSedType(), is("LA_BUC_04"));
        assertThat(hoveddokument.getFilinfoListe(), not(empty()));
        assertThat(hoveddokument.getFilinfoListe().get(0).getArkivFilType(), is(ArkivFilType.PDFA));
        assertThat(hoveddokument.getFilinfoListe().get(0).getVariantFormat(), is(VariantFormat.ARKIV));
    }

    private SedHendelse getSedHendelseStub() {
        return SedHendelse.builder()
                .id(1L)
                .bucType("LA_BUC_04")
                .navBruker("05059905050")
                .rinaDokumentId("123123123")
                .rinaDokumentVersjon("4.1")
                .rinaSakId("11111")
                .sedId("1")
                .sedType("A009")
                .sektorKode("MED")
                .avsenderId("123")
                .avsenderNavn("123Navn")
                .mottakerId("321")
                .mottakerNavn("321Navn")
                .build();
    }

    private Sak getSakStub() {
        return Sak.builder()
                .aktoerId("123")
                .tema("MED")
                .applikasjon("MELOSYS")
                .fagsakNr("123221")
                .opprettetAv("Me")
                .id("123")
                .opprettetTidspunkt(ZonedDateTime.now())
                .orgnr("111111111")
                .build();

    }

    private ParticipantInfo getReceiverInfoStub() {
        return ParticipantInfo.builder()
                .id("123id")
                .name("123name")
                .build();
    }
}