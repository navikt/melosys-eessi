package no.nav.melosys.eessi.integration.eux.rina_api;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.Institusjon;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.bucinfo.BucInfo;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.*;
import no.nav.melosys.eessi.security.SystemContextClientRequestInterceptor;
import no.nav.melosys.eessi.service.sts.RestStsService;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class EuxConsumerTest {

    private EuxConsumer euxConsumer;

    private MockRestServiceServer server;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        EuxConsumerProducer consumerConfig = new EuxConsumerProducer(null);
        SystemContextClientRequestInterceptor interceptor = new SystemContextClientRequestInterceptor(mock(RestStsService.class));

        RestTemplate restTemplate = consumerConfig.euxRestTemplate(new RestTemplateBuilder(), interceptor);
        euxConsumer = new EuxConsumer(restTemplate, objectMapper);
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void hentBuC_returnerObjekt() throws Exception {

        URL jsonUrl = getClass().getClassLoader().getResource("mock/buc.json");
        assertThat(jsonUrl).isNotNull();
        String buc = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));

        String id = "1234";

        server.expect(requestTo("/buc/" + id))
                .andRespond(withSuccess(buc , MediaType.APPLICATION_JSON));

        BUC response = euxConsumer.hentBuC(id);
        assertThat(response).isNotNull();
        assertThat(response.getDocuments()).isNotEmpty();
        assertThat(response.getDocuments().get(0).getId()).isEqualTo("93f022ea50e54c08bbdb85290a5fb23d");
        assertThat(response.getBucType()).isEqualTo("LA_BUC_01");
    }

    @Test
    public void opprettBuC_returnererId() throws Exception {
        String id = "1234";
        String buc = "LA_BUC_04";
        server.expect(requestTo("/buc?BuCType=" + buc))
                .andRespond(withSuccess("1234", MediaType.APPLICATION_JSON));

        String response = euxConsumer.opprettBuC(buc);
        assertThat(response).isEqualTo(id);
    }

    @Test
    public void slettBuC_ingenRetur() throws Exception {
        String id = "1234";
        server.expect(requestTo("/buc/" + id))
                .andRespond(withSuccess());

        euxConsumer.slettBuC(id);
    }

    @Test
    public void settMottaker_ingenRetur() throws Exception {
        String id = "1234";
        String mottaker = "NAV_DANMARK_123";
        server.expect(requestTo("/buc/" + id + "/bucdeltakere?MottakerId=" + mottaker))
                .andRespond(withSuccess("1234", MediaType.APPLICATION_JSON));

        euxConsumer.settMottaker(id, mottaker);
    }

    @Test
    public void hentBucTypePerSektor_returnerListe() throws Exception {

        List<String> forventetRetur = Lists.newArrayList("en", "to", "tre");

        server.expect(requestTo("/buctypepersektor"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(forventetRetur), MediaType.APPLICATION_JSON));

        List<String> resultat = euxConsumer.bucTypePerSektor();
        assertThat(resultat).isEqualTo(forventetRetur);
    }

    @Test
    public void hentInstitusjoner_forventListe() throws Exception {
        URL jsonUrl = getClass().getClassLoader().getResource("mock/institusjon_liste.json");
        assertThat(jsonUrl).isNotNull();
        String institusjonerString = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));

        String buctype = "LA_BUC_04";
        String landkode = "NO";

        server.expect(requestTo("/institusjoner?BuCType=" + buctype + "&LandKode=" + landkode))
                .andRespond(withSuccess(institusjonerString, MediaType.APPLICATION_JSON));

        List<Institusjon> resultat = euxConsumer.hentInstitusjoner(buctype, landkode);
        assertThat(resultat).isNotNull();

        Institusjon institusjon = resultat.get(0);
        assertThat(institusjon).isNotNull();
        assertThat(institusjon.getId()).isEqualTo("LT:123123");
        assertThat(institusjon.getTilegnetBucs().size()).isEqualTo(2);
    }

    @Test
    public void hentKodeverk_forventJson() throws Exception {
        Map<String, Object> forventetRetur = Maps.newHashMap();
        forventetRetur.put("string", "value");
        forventetRetur.put("int", 1L);

        String kodeverk = "Test";

        server.expect(requestTo("/kodeverk?Kodeverk=" + kodeverk))
                .andRespond(withSuccess(objectMapper.writeValueAsString(forventetRetur), MediaType.APPLICATION_JSON));

        JsonNode resultat = euxConsumer.hentKodeverk(kodeverk);
        assertThat(resultat.has("string")).isTrue();
        assertThat(resultat.has("int")).isTrue();
    }

    @Test
    public void hentMuligeAksjoner_forventJson() throws Exception {
        Map<String, Object> forventetRetur = Maps.newHashMap();
        forventetRetur.put("string", "value");
        forventetRetur.put("int", 1L);

        String id = "1234";
        server.expect(requestTo("/buc/" + id + "/muligeaksjoner"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(forventetRetur), MediaType.APPLICATION_JSON));

        JsonNode resultat = euxConsumer.hentMuligeAksjoner(id);
        assertThat(resultat.has("string")).isTrue();
        assertThat(resultat.has("int")).isTrue();
    }

    @Test
    public void opprettBucOgSed_forventString() throws Exception {
        String buc = "buc", mottaker = "NAV";
        SED sed = new SED();

        Map<String, String> forventetResultat = Maps.newHashMap();
        forventetResultat.put("documentId", "123ewq123ewq");
        forventetResultat.put("caseId", "rewf24");

        server.expect(requestTo("/buc/sed?BucType=" + buc + "&MottakerId=" + mottaker))
                .andRespond(
                        withSuccess(objectMapper.writeValueAsString(forventetResultat), MediaType.APPLICATION_JSON));

        Map resultat = euxConsumer.opprettBucOgSed(buc, mottaker, sed);
        assertThat(resultat).isEqualTo(forventetResultat);
    }

    @Test
    public void opprettBucOgSedMedVedlegg_forventString() throws Exception {
        String buc = "buc", fagsak = "123", mottaker = "NAV", filtype = "virus.exe", korrelasjon = "111", vedlegg = "vedlegg";
        SED sed = new SED();

        Map<String, String> forventetResultat = Maps.newHashMap();
        forventetResultat.put("documentId", "123ewq123ewq");
        forventetResultat.put("caseId", "rewf24");
        forventetResultat.put("attachmentId", "ffrewf24");

        server.expect(requestTo("/buc/sed/vedlegg?BuCType=" + buc + "&MottakerID=" + mottaker + "&FilType=" + filtype))
                .andRespond(withSuccess(objectMapper.writeValueAsString(forventetResultat), MediaType.APPLICATION_JSON));

        Map resultat = euxConsumer.opprettBucOgSedMedVedlegg(buc, mottaker, filtype, sed, vedlegg.getBytes());
        assertThat(resultat).isEqualTo(forventetResultat);
    }

    @Test
    public void finnRinaSaker_forventJson() throws Exception {
        URL jsonUrl = getClass().getClassLoader().getResource("mock/bucinfo.json");
        assertThat(jsonUrl).isNotNull();
        String forventetRetur = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));

        String fnr = "123", fornavn = "Andre", etternavn = "Måns", fødselsdato = "12-12-12", saksnummer = "123",
                bucType = "LA_BUC_04", status = "ferdig";

        //Må encode uri, da non-ascii blir escaped
        String uri = UriComponentsBuilder
                .fromUriString("/rinasaker?fødselsnummer=" + fnr + "&fornavn=" + fornavn + "&etternavn=" + etternavn +
                        "&fødselsdato=" + fødselsdato + "&rinasaksnummer=" + saksnummer + "&buctype=" + bucType
                        + "&status=" + status).toUriString();

        server.expect(requestTo(uri))
                .andRespond(withSuccess(forventetRetur, MediaType.APPLICATION_JSON));

        List<BucInfo> resultat = euxConsumer
                .finnRinaSaker(fnr, fornavn, etternavn, fødselsdato, saksnummer, bucType, status);
        assertThat(resultat).isNotNull();
        assertThat(resultat).isNotEmpty();
        assertThat(resultat.size()).isEqualTo(2);
        assertThat(resultat.get(0).getId()).isEqualTo("100485");
    }

    @Test
    public void hentSedA001_forventSed() throws Exception {
        String id = "123";
        String dokumentId = "312";

        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA001.json");
        assertNotNull(jsonUrl);
        String sed = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId))
                .andRespond(withSuccess(sed, MediaType.APPLICATION_JSON));

        SED resultat = euxConsumer.hentSed(id, dokumentId);
        assertThat(resultat).isNotNull();
        assertThat(resultat.getNav()).isNotNull();
        assertThat(resultat.getMedlemskap()).isNotNull();
        assertThat(resultat.getSedType()).isEqualTo(SedType.A001.name());
        assertThat(resultat.getMedlemskap().getClass()).isEqualTo(MedlemskapA001.class);

        MedlemskapA001 medlemskapA001 = (MedlemskapA001) resultat.getMedlemskap();
        assertThat(medlemskapA001.getForrigesoeknad().get(0).getDato()).isEqualTo("2017-12-01");
    }

    @Test
    public void hentSedA003_forventSed() throws Exception {
        String id = "123";
        String dokumentId = "312";

        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA003.json");
        assertThat(jsonUrl).isNotNull();
        String sed = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId))
                .andRespond(withSuccess(sed, MediaType.APPLICATION_JSON));

        SED resultat = euxConsumer.hentSed(id, dokumentId);
        assertThat(resultat).isNotNull();
        assertThat(resultat.getNav()).isNotNull();
        assertThat(resultat.getMedlemskap()).isNotNull();
        assertThat(resultat.getSedType()).isEqualTo(SedType.A003.name());
        assertThat(resultat.getMedlemskap().getClass()).isEqualTo(MedlemskapA003.class);

        MedlemskapA003 medlemskap = (MedlemskapA003) resultat.getMedlemskap();
        assertThat( medlemskap.getVedtak().getGjelderperiode().getSluttdato()).isEqualTo("2017-12-01");
    }

    @Test
    public void hentSedA008_forventSed() throws Exception {
        String id = "123";
        String dokumentId = "312";

        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA008.json");
        assertThat(jsonUrl).isNotNull();
        String sed = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId))
                .andRespond(withSuccess(sed, MediaType.APPLICATION_JSON));

        SED resultat = euxConsumer.hentSed(id, dokumentId);
        assertThat(resultat).isNotNull();
        assertThat(resultat.getNav()).isNotNull();
        assertThat(resultat.getSedType()).isEqualTo(SedType.A008.name());
        assertThat(resultat.getMedlemskap()).isNotNull();
        assertThat(resultat.getMedlemskap().getClass()).isEqualTo(MedlemskapA008.class);

        MedlemskapA008 medlemskapA008 = (MedlemskapA008) resultat.getMedlemskap();
        assertThat(medlemskapA008.getBruker().getArbeidiflereland().getBosted().getLand()).isEqualTo("SE");
    }

    @Test
    public void hentSedA009_forventSed() throws Exception {
        String id = "123";
        String dokumentId = "312";

        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA009.json");
        assertNotNull(jsonUrl);
        String sed = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId))
                .andRespond(withSuccess(sed, MediaType.APPLICATION_JSON));

        SED resultat = euxConsumer.hentSed(id, dokumentId);
        assertThat(resultat).isNotNull();
        assertThat(resultat.getNav()).isNotNull();
        assertThat(resultat.getSedType()).isEqualTo(SedType.A009.name());
        assertThat(resultat.getMedlemskap()).isNotNull();
        assertThat(resultat.getMedlemskap().getClass()).isEqualTo(MedlemskapA009.class);
    }

    @Test
    public void hentSedA010_forventSed() throws Exception {
        String id = "123";
        String dokumentId = "312";

        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA010.json");
        assertNotNull(jsonUrl);
        String sed = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId))
                .andRespond(withSuccess(sed, MediaType.APPLICATION_JSON));

        SED resultat = euxConsumer.hentSed(id, dokumentId);
        assertThat(resultat).isNotNull();
        assertThat(resultat.getNav()).isNotNull();
        assertThat(resultat.getSedType()).isEqualTo(SedType.A010.name());
        assertThat(resultat.getMedlemskap()).isNotNull();
        assertThat(resultat.getMedlemskap().getClass()).isEqualTo(MedlemskapA010.class);
    }

    @Test
    public void hentSedX001_forventSed() throws Exception {
        String id = "123";
        String dokumentId = "312";

        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedX001.json");
        assertThat(jsonUrl).isNotNull();
        String sed = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId))
                .andRespond(withSuccess(sed, MediaType.APPLICATION_JSON));

        SED resultat = euxConsumer.hentSed(id, dokumentId);
        assertThat(resultat).isNotNull();
        assertThat(resultat.getNav()).isNotNull();
        assertThat(resultat.getSedType()).isEqualTo(SedType.X001.name());
        assertThat(resultat.getMedlemskap()).isNull();
    }

    @Test
    public void hentSedPdf_forventPdf() throws Exception {
        String id = "123", dokumentId = "123321";

        byte[] forventetRetur = "teststring".getBytes();

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId + "/pdf"))
                .andRespond(withSuccess(forventetRetur, MediaType.APPLICATION_OCTET_STREAM));

        byte[] resultat = euxConsumer.hentSedPdf(id, dokumentId);
        assertThat(resultat).isEqualTo(forventetRetur);
    }

    @Test
    public void genererPdfFraSed_forventPdf() throws IntegrationException {
        SED sed = new SED();
        byte[] forventetRetur = "teststring".getBytes();

        server.expect(requestTo("/sed/pdf"))
                .andRespond(withSuccess(forventetRetur, MediaType.APPLICATION_PDF));

        byte[] resultat = euxConsumer.genererPdfFraSed(sed);
        assertThat(forventetRetur).isEqualTo(resultat);
    }

    @Test
    public void opprettSed_forventId() throws Exception {
        String id = "123";
        String korrelasjonId = "312";
        SED sed = new SED();

        String forventetRetur = "123321";

        server.expect(requestTo("/buc/" + id + "/sed?KorrelasjonsId=" + korrelasjonId))
                .andRespond(withSuccess(forventetRetur, MediaType.APPLICATION_JSON));

        String resultat = euxConsumer.opprettSed(id, korrelasjonId, sed);
        assertThat(resultat).isEqualTo(forventetRetur);
    }

    @Test
    public void oppdaterSed_ingenRetur() throws Exception {
        String id = "123";
        String korrelasjonId = "312";
        String dokumentId = "1111";
        SED sed = new SED();

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId + "?KorrelasjonsId=" + korrelasjonId))
                .andRespond(withSuccess());

        euxConsumer.oppdaterSed(id, korrelasjonId, dokumentId, sed);
    }

    @Test
    public void slettSed_ingenRetur() throws Exception {
        String id = "123";
        String dokumentId = "1122233";

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId))
                .andRespond(withSuccess());

        euxConsumer.slettSed(id, dokumentId);
    }

    @Test
    public void sendSed_ingenRetur() throws Exception {
        String id = "123";
        String korrelasjonsId = "111";
        String dokumentId = "22";

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId + "/send?KorrelasjonsId=" + korrelasjonsId))
                .andRespond(withSuccess());

        euxConsumer.sendSed(id, korrelasjonsId, dokumentId);
    }

    @Test
    public void hentTilgjengeligeSedType_forventListeString() throws Exception, JsonProcessingException {
        String id = "123";

        List<String> forventetRetur = Lists.newArrayList("en", "to", "tre");

        server.expect(requestTo("/buc/" + id + "/sedtyper"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(forventetRetur), MediaType.APPLICATION_JSON));

        List<String> resultat = euxConsumer.hentTilgjengeligeSedTyper(id);
        assertThat(resultat).isEqualTo(forventetRetur);
    }

    @Test
    public void hentVedlegg_ForventByteArray() throws Exception {
        String id = "123";
        String dokumentId = "123321";
        String vedleggId = "2222";

        byte[] forventetRetur = "returverdi".getBytes();

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId + "/vedlegg/" + vedleggId))
                .andRespond(withSuccess(forventetRetur, MediaType.APPLICATION_OCTET_STREAM));

        byte[] resultat = euxConsumer.hentVedlegg(id, dokumentId, vedleggId);
        assertThat(resultat).isEqualTo(forventetRetur);
    }

    @Test
    public void leggTilVedlegg_forventId() throws Exception {
        String id = "123";
        String dokumentId = "123321";
        String filtype = "virus.exe";

        String forventetRetur = "{}";

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId + "/vedlegg?Filtype=" + filtype))
                .andRespond(withSuccess(forventetRetur, MediaType.APPLICATION_JSON));

        String resultat = euxConsumer.leggTilVedlegg(id, dokumentId, filtype, "vedlegg");
        assertThat(resultat).isEqualTo(forventetRetur);
    }

    @Test
    public void slettVedlegg_ingenRetur() throws Exception {
        String id = "123";
        String dokumentId = "123321";
        String vedleggId = "2222";

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId + "/vedlegg/" + vedleggId))
                .andRespond(withSuccess());

        euxConsumer.slettVedlegg(id, dokumentId, vedleggId);
    }

    @Test(expected = IntegrationException.class)
    public void exceptionHåndtering_utenFeilmeldingBody_forventExceptionMed400BadRequest() throws Exception {
        String id = "123";
        String dokumentId = "123321";
        String vedleggId = "2222";

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId + "/vedlegg/" + vedleggId))
                .andRespond(withBadRequest());

        euxConsumer.slettVedlegg(id, dokumentId, vedleggId);
    }

    @Test
    public void setSakSensitiv_ingenResponseEllerException() throws Exception {

        String id ="123";
        server.expect(requestTo("/buc/" + id + "/sensitivsak"))
                .andRespond(withSuccess());

        euxConsumer.setSakSensitiv(id);
    }

    @Test
    public void fjernSakSensitiv_ingenResponseEllerException() throws Exception {

        String id ="123";
        server.expect(requestTo("/buc/" + id + "/sensitivsak"))
                .andRespond(withSuccess());

        euxConsumer.fjernSakSensitiv(id);
    }

    @Test
    public void hentDeltakere_jsonResponse() throws Exception {
        String id ="123";
        server.expect(requestTo("/buc/" + id + "/bucdeltakere"))
                .andRespond(withSuccess("{\"hei\":\"ho\"}", MediaType.APPLICATION_JSON));

        JsonNode jsonNode = euxConsumer.hentDeltagere(id);

        assertThat(jsonNode.get("hei").textValue()).isEqualTo("ho");
    }
}
