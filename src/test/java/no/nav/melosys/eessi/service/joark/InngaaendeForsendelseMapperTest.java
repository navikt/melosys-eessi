package no.nav.melosys.eessi.service.joark;

import java.util.AbstractMap;
import java.util.HashMap;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.Aktoer;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.MottaInngaaendeForsendelseRequest;
import no.nav.eessi.basis.SedMottatt;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.service.dokkat.DokkatSedInfo;
import org.junit.Test;
import static no.nav.dok.tjenester.mottainngaaendeforsendelse.DokumentVariant.ArkivFilType.PDFA;
import static no.nav.dok.tjenester.mottainngaaendeforsendelse.DokumentVariant.VariantFormat.ARKIV;
import static no.nav.melosys.eessi.service.joark.InngaaendeForsendelseMapper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class InngaaendeForsendelseMapperTest {

    @Test
    public void createMottaInngaaendeForsendelseRequest_expectCorrectlyFormattedRequest() {
        String aktoerId = "123123123";

        SedMottatt sedMottatt = new SedMottatt();
        sedMottatt.setSedId("123456");

        Sak sak = new Sak();
        sak.setTema("MED");

        DokkatSedInfo dokkatSedInfo = new DokkatSedInfo();
        dokkatSedInfo.setDokumentTittel("Dokumenttittel");
        dokkatSedInfo.setDokumenttypeId("DokumenttypeId");

        ParticipantInfo senderInfo = ParticipantInfo.builder()
                .name("NAVT002")
                .id("NO:NAVT002")
                .build();

        byte[] pdf = SedDocumentStub.getPdfStub();

        MottaInngaaendeForsendelseRequest mottaInngaaendeForsendelseRequest =
                createMottaInngaaendeForsendelseRequest(aktoerId, sedMottatt, sak, dokkatSedInfo, senderInfo, pdf);

        assertThat(mottaInngaaendeForsendelseRequest
                .getForsokEndeligJF(), is(false));
        assertThat(mottaInngaaendeForsendelseRequest.getForsendelseInformasjon()
                .getBruker(), not(nullValue()));
        assertThat(mottaInngaaendeForsendelseRequest.getForsendelseInformasjon()
                .getAvsender(), not(nullValue()));
        assertThat(mottaInngaaendeForsendelseRequest.getForsendelseInformasjon()
                .getTema(), is("MED"));
        assertThat(mottaInngaaendeForsendelseRequest.getForsendelseInformasjon()
                .getKanalReferanseId(), is("123456"));
        assertThat(mottaInngaaendeForsendelseRequest.getForsendelseInformasjon()
                .getMottaksKanal(), is("EESSI"));
        assertThat(mottaInngaaendeForsendelseRequest.getForsendelseInformasjon()
                .getTittel(), is("Dokumenttittel"));
        assertThat(mottaInngaaendeForsendelseRequest.getDokumentInfoHoveddokument()
                .getDokumentTypeId(), is("DokumenttypeId"));
        assertThat(mottaInngaaendeForsendelseRequest.getDokumentInfoHoveddokument().getDokumentVariant().get(0)
                .getArkivFilType(), is(PDFA));
        assertThat(mottaInngaaendeForsendelseRequest.getDokumentInfoHoveddokument().getDokumentVariant().get(0)
                .getVariantFormat(), is(ARKIV));
        assertThat(mottaInngaaendeForsendelseRequest.getDokumentInfoHoveddokument().getDokumentVariant().get(0)
                .getDokument(), is(pdf));
    }

    @Test
    public void person_expectAktoerWithAdditionalProperties() {
        String ident = "1122334455";

        Aktoer aktoer = person(ident);

        assertThat(aktoer, not(nullValue()));
        assertThat(aktoer.getAdditionalProperties(), not(nullValue()));
        assertThat(aktoer.getAdditionalProperties().get("aktoer"), not(nullValue()));
        assertThat(aktoer.getAdditionalProperties().get("aktoer"), instanceOf(AbstractMap.SimpleImmutableEntry.class));

        AbstractMap.SimpleImmutableEntry aktoerEntry = (AbstractMap.SimpleImmutableEntry) aktoer.getAdditionalProperties().get("aktoer");
        assertThat(aktoerEntry.getKey(), is("person"));
        assertThat(aktoerEntry.getValue(), instanceOf(AbstractMap.SimpleImmutableEntry.class));

        AbstractMap.SimpleImmutableEntry personEntry = (AbstractMap.SimpleImmutableEntry) aktoerEntry.getValue();
        assertThat(personEntry.getKey(), is("ident"));
        assertThat(personEntry.getValue(), is("1122334455"));
    }

    @Test
    public void organisasjon_expectAktoerWithAdditionalProperties() {
        String navn = "org";
        String orgnr = "5544332211";

        Aktoer aktoer = organisasjon(orgnr, navn);

        assertThat(aktoer, not(nullValue()));
        assertThat(aktoer.getAdditionalProperties(), not(nullValue()));
        assertThat(aktoer.getAdditionalProperties().get("aktoer"), not(nullValue()));
        assertThat(aktoer.getAdditionalProperties().get("aktoer"), instanceOf(AbstractMap.SimpleImmutableEntry.class));

        AbstractMap.SimpleImmutableEntry aktoerEntry = (AbstractMap.SimpleImmutableEntry) aktoer.getAdditionalProperties().get("aktoer");
        assertThat(aktoerEntry.getKey(), is("organisasjon"));
        assertThat(aktoerEntry.getValue(), instanceOf(HashMap.class));

        HashMap organisasjonEntry = (HashMap) aktoerEntry.getValue();
        assertThat(organisasjonEntry.get("navn"), is("org"));
        assertThat(organisasjonEntry.get("orgnr"), is("5544332211"));
    }

    @Test
    public void organisasjon_withNoOrgnr_expectAktoerWithDefaultValues() {
        String navn = "org";

        Aktoer aktoer = organisasjon(null, navn);

        assertThat(aktoer, not(nullValue()));
        assertThat(aktoer.getAdditionalProperties(), not(nullValue()));
        assertThat(aktoer.getAdditionalProperties().get("aktoer"), not(nullValue()));
        assertThat(aktoer.getAdditionalProperties().get("aktoer"), instanceOf(AbstractMap.SimpleImmutableEntry.class));

        AbstractMap.SimpleImmutableEntry aktoerEntry = (AbstractMap.SimpleImmutableEntry) aktoer.getAdditionalProperties().get("aktoer");
        assertThat(aktoerEntry.getKey(), is("organisasjon"));
        assertThat(aktoerEntry.getValue(), instanceOf(HashMap.class));

        HashMap organisasjonEntry = (HashMap) aktoerEntry.getValue();
        assertThat(organisasjonEntry.get("navn"), is("avsender ikke tilgjengelig"));
        assertThat(organisasjonEntry.get("orgnr"), is("avsender ikke tilgjengelig"));
    }

    @Test
    public void organisasjon_withNoOrgnrOrNavn_expectAktoerWithDefaultValues() {
        Aktoer aktoer = organisasjon(null, null);

        assertThat(aktoer, not(nullValue()));
        assertThat(aktoer.getAdditionalProperties(), not(nullValue()));
        assertThat(aktoer.getAdditionalProperties().get("aktoer"), not(nullValue()));
        assertThat(aktoer.getAdditionalProperties().get("aktoer"), instanceOf(AbstractMap.SimpleImmutableEntry.class));

        AbstractMap.SimpleImmutableEntry aktoerEntry = (AbstractMap.SimpleImmutableEntry) aktoer.getAdditionalProperties().get("aktoer");
        assertThat(aktoerEntry.getKey(), is("organisasjon"));
        assertThat(aktoerEntry.getValue(), instanceOf(HashMap.class));

        HashMap organisasjonEntry = (HashMap) aktoerEntry.getValue();
        assertThat(organisasjonEntry.get("navn"), is("avsender ikke tilgjengelig"));
        assertThat(organisasjonEntry.get("orgnr"), is("avsender ikke tilgjengelig"));
    }
}