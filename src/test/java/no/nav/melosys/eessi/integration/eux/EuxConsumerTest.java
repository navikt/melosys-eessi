package no.nav.melosys.eessi.integration.eux;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import no.nav.melosys.eessi.integration.eux.dto.Institusjon;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.SedType;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA001;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(MockitoJUnitRunner.class)
public class EuxConsumerTest {

    @Spy
    private RestTemplate restTemplate;

    private EuxConsumer euxConsumer;

    private MockRestServiceServer server;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        euxConsumer = new EuxConsumer(restTemplate);
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void hentBuC_returnerObjekt() throws Exception {
        String id = "1234";
        server.expect(requestTo("/buc/" + id))
                .andRespond(withSuccess("1234", MediaType.APPLICATION_JSON));

        JsonNode response = euxConsumer.hentBuC(id);
        assertNotNull(response);
    }

    @Test
    public void opprettBuC_returnererId() throws Exception {
        String id = "1234";
        String buc = "LA_BUC_04";
        server.expect(requestTo("/buc?BuCType=" + buc))
                .andRespond(withSuccess("1234", MediaType.APPLICATION_JSON));

        String response = euxConsumer.opprettBuC(buc);
        assertEquals(id, response);
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
        assertEquals(resultat, forventetRetur);
    }

    @Test
    public void hentInstitusjoner_forventListe() throws Exception {
        URL jsonUrl = getClass().getClassLoader().getResource("mock/institusjon_liste.json");
        assertNotNull(jsonUrl);
        String institusjonerString = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));

        String buctype = "LA_BUC_04";
        String landkode = "NO";

        server.expect(requestTo("/institusjoner?BuCType=" + buctype + "&LandKode=" + landkode))
                .andRespond(withSuccess(institusjonerString, MediaType.APPLICATION_JSON));

        List<Institusjon> resultat = euxConsumer.hentInstitusjoner(buctype, landkode);
        assertNotNull(resultat);

        Institusjon institusjon = resultat.get(0);
        assertNotNull(institusjon);
        assertEquals("LT:123123", institusjon.getId());
        assertEquals(2, institusjon.getTilegnetBucs().size());
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
        assertTrue(resultat.has("string"));
        assertTrue(resultat.has("int"));
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
        assertTrue(resultat.has("string"));
        assertTrue(resultat.has("int"));
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
        assertEquals(forventetResultat, resultat);
    }

    @Test
    public void opprettBucOgSedMedVedlegg_forventString() throws Exception {
        String buc = "buc", fagsak = "123", mottaker = "NAV", filtype = "virus.exe", korrelasjon = "111", vedlegg = "vedlegg";
        SED sed = new SED();

        Map<String, String> forventetResultat = Maps.newHashMap();
        forventetResultat.put("documentId", "123ewq123ewq");
        forventetResultat.put("caseId", "rewf24");
        forventetResultat.put("attachmentId", "ffrewf24");

        server.expect(requestTo("/buc/sed/vedlegg?BuCType=" + buc + "&FagSakNummer=" + fagsak +
                "&MottakerID=" + mottaker + "&FilType=" + filtype + "&KorrelasjonsId=" + korrelasjon))
                .andRespond(
                        withSuccess(objectMapper.writeValueAsString(forventetResultat), MediaType.APPLICATION_JSON));

        Map resultat = euxConsumer.opprettBucOgSedMedVedlegg(buc, fagsak, mottaker, filtype, korrelasjon, sed, vedlegg);
        assertEquals(forventetResultat, resultat);
    }

    @Test
    public void finnRinaSaker_forventJson() throws Exception {
        Map<String, Object> forventetRetur = Maps.newHashMap();
        forventetRetur.put("string", "value");
        forventetRetur.put("int", 1L);

        String fnr = "123", fornavn = "Andre", etternavn = "Måns", fødselsdato = "12-12-12", saksnummer = "123",
                bucType = "LA_BUC_04", status = "ferdig";

        //Må encode uri, da non-ascii blir escaped
        String uri = UriComponentsBuilder
                .fromUriString("/rinasaker?Fødselsnummer=" + fnr + "&Fornavn=" + fornavn + "&Etternavn=" + etternavn +
                        "&Fødselsdato=" + fødselsdato + "&RINASaksnummer=" + saksnummer + "&BuCType=" + bucType
                        + "&Status=" + status).toUriString();

        server.expect(requestTo(uri))
                .andRespond(withSuccess(objectMapper.writeValueAsString(forventetRetur), MediaType.APPLICATION_JSON));

        JsonNode resultat = euxConsumer
                .finnRinaSaker(fnr, fornavn, etternavn, fødselsdato, saksnummer, bucType, status);
        assertTrue(resultat.has("string"));
        assertTrue(resultat.has("int"));
    }

    @Test
    public void hentSedA001_forventSed() throws Exception {
        String id = "123";
        String dokumentId = "312";

        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA001.json");
        assertNotNull(jsonUrl);
        String sed = IOUtils.toString(jsonUrl);

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId))
                .andRespond(withSuccess(sed, MediaType.APPLICATION_JSON));

        SED resultat = euxConsumer.hentSed(id, dokumentId);
        assertNotNull(resultat);
        assertNotNull(resultat.getNav());
        assertNotNull(resultat.getMedlemskap());
        assertEquals(SedType.A001.name(), resultat.getSed());
        assertEquals(MedlemskapA001.class, resultat.getMedlemskap().getClass());
    }

    @Test
    public void hentSedA009_forventSed() throws Exception {
        String id = "123";
        String dokumentId = "312";

        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA009.json");
        assertNotNull(jsonUrl);
        String sed = IOUtils.toString(jsonUrl);

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId))
                .andRespond(withSuccess(sed, MediaType.APPLICATION_JSON));

        SED resultat = euxConsumer.hentSed(id, dokumentId);
        assertNotNull(resultat);
        assertNotNull(resultat.getNav());
        assertEquals(SedType.A009.name(), resultat.getSed());
        assertNotNull(resultat.getMedlemskap());
        assertEquals(MedlemskapA009.class, resultat.getMedlemskap().getClass());
    }

    @Test
    public void hentSedA010_forventSed() throws Exception {
        String id = "123";
        String dokumentId = "312";

        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA010.json");
        assertNotNull(jsonUrl);
        String sed = IOUtils.toString(jsonUrl);

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId))
                .andRespond(withSuccess(sed, MediaType.APPLICATION_JSON));

        SED resultat = euxConsumer.hentSed(id, dokumentId);
        assertNotNull(resultat);
        assertNotNull(resultat.getNav());
        assertEquals(SedType.A010.name(), resultat.getSed());
        assertNotNull(resultat.getMedlemskap());
        assertEquals(MedlemskapA010.class, resultat.getMedlemskap().getClass());
    }

    @Test
    public void hentSedPdf_forventPdf() throws Exception {
        String id = "123", dokumentId = "123321";

        byte[] forventetRetur = "teststring".getBytes();

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId + "/pdf"))
                .andRespond(withSuccess(forventetRetur, MediaType.APPLICATION_OCTET_STREAM));

        byte[] resultat = euxConsumer.hentSedPdf(id, dokumentId);
        assertTrue(Arrays.equals(forventetRetur, resultat));
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
        assertEquals(forventetRetur, resultat);
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
        assertEquals(forventetRetur, resultat);
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
        assertTrue(Arrays.equals(forventetRetur, resultat));
    }

    @Test
    public void leggTilVedlegg_forventId() throws Exception {
        String id = "123";
        String dokumentId = "123321";
        String filtype = "virus.exe";

        String forventetRetur = "returverdi#123";

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId + "/vedlegg?Filtype=" + filtype))
                .andRespond(withSuccess(forventetRetur, MediaType.APPLICATION_JSON));

        String resultat = euxConsumer.leggTilVedlegg(id, dokumentId, filtype, "vedlegg");
        assertEquals(forventetRetur, resultat);
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
    public void exceptionHåndtering_forventMelsoysException() throws Exception {
        String id = "123";
        String dokumentId = "123321";
        String vedleggId = "2222";

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId + "/vedlegg/" + vedleggId))
                .andRespond(withBadRequest());

        euxConsumer.slettVedlegg(id, dokumentId, vedleggId);
    }


}
