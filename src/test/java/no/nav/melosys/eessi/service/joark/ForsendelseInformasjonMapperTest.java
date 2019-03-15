package no.nav.melosys.eessi.service.joark;

import java.time.ZonedDateTime;
import no.nav.dokarkivsed.api.v1.*;
import no.nav.eessi.basis.SedSendt;
import no.nav.melosys.eessi.integration.gsak.Sak;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ForsendelseInformasjonMapperTest {

    private ForsendelseInformasjonMapper mapper = new ForsendelseInformasjonMapper();

    @Test
    public void createForsendelse_expectValidForsendelsesInformasjon() {
        ForsendelsesInformasjon res = mapper.createForsendelse("aktoerid", getSedSendtStub(), getSakStub(),getReceiverInfoStub());
        assertThat(res, not(nullValue()));
        assertThat(res.getBruker(), not(nullValue()));
        assertThat(res.getBruker(), instanceOf(Person.class));
        assertThat(res.getMottaker(), instanceOf(Organisasjon.class));
        assertThat(res.getTema(), is("MED"));
        assertThat(res.getBucId(), is("11111"));
    }

    @Test
    public void createForsendelse_expectIkkeTilgjengligMottakerAndNoArkivsakAndNoBruker() {
        ForsendelsesInformasjon res = mapper.createForsendelse("", getSedSendtStub(), null, null);
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

    private SedSendt getSedSendtStub() {
        return SedSendt.newBuilder()
                .setId(1L)
                .setBucType("LA_BUC_04")
                .setNavBruker("05059905050")
                .setRinaDokumentId("123123123")
                .setRinaDokumentVersjon("4.1")
                .setRinaSakId("11111")
                .setSedId("1")
                .setSedType("A009")
                .setSektorKode("MED")
                .setAvsenderId("123")
                .setAvsenderNavn("123Navn")
                .setMottakerId("321")
                .setMottakerNavn("321Navn")
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