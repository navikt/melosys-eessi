package no.nav.melosys.eessi.integration.eux.rina_api;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.Institusjon;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.SedVedlegg;
import no.nav.melosys.eessi.models.buc.*;
import no.nav.melosys.eessi.models.bucinfo.BucInfo;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.*;
import no.nav.melosys.eessi.models.sed.nav.Nav;
import no.nav.melosys.eessi.security.SystemContextClientRequestInterceptor;
import no.nav.melosys.eessi.service.sts.RestStsService;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class EuxConsumerTest {

    private EuxConsumer euxConsumer;

    private MockRestServiceServer server;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        EuxConsumerProducer consumerConfig = new EuxConsumerProducer(null);
        SystemContextClientRequestInterceptor interceptor = new SystemContextClientRequestInterceptor(mock(RestStsService.class));

        RestTemplate restTemplate = consumerConfig.euxRestTemplate(new RestTemplateBuilder(), interceptor);
        euxConsumer = new EuxConsumer(restTemplate, objectMapper);
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void hentBUC_returnerObjekt() throws Exception {

        URL jsonUrl = getClass().getClassLoader().getResource("mock/buc.json");
        assertThat(jsonUrl).isNotNull();
        String buc = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));

        String id = "1234";

        server.expect(requestTo("/buc/" + id))
                .andRespond(withSuccess(buc, MediaType.APPLICATION_JSON));

        BUC response = euxConsumer.hentBUC(id);
        assertThat(response).isNotNull();
        assertThat(response.getDocuments()).isNotEmpty();
        assertThat(response.getDocuments().get(0).getId()).isEqualTo("93f022ea50e54c08bbdb85290a5fb23d");
        assertThat(response.getBucType()).isEqualTo("LA_BUC_01");
        assertThat(response.getBucVersjon()).isEqualTo("v4.1");

        assertThat(response.getDocuments())
                .flatExtracting(Document::getConversations)
                .flatExtracting(Conversation::getParticipants)
                .extracting(Participant::getOrganisation)
                .extracting(Organisation::getId)
                .containsAll(List.of("NO:NAVT003", "NO:NAVT007"));
    }

    @Test
    void opprettBUC_returnererId() {
        String id = "1234";
        String buc = "LA_BUC_04";
        server.expect(requestTo("/buc?BuCType=" + buc))
                .andRespond(withSuccess("1234", MediaType.APPLICATION_JSON));

        String response = euxConsumer.opprettBUC(buc);
        assertThat(response).isEqualTo(id);
    }

    @Test
    void slettBUC_ingenRetur() {
        String id = "1234";
        server.expect(requestTo("/buc/" + id))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        euxConsumer.slettBUC(id);
    }

    @Test
    void settMottaker_ingenRetur() {
        String rinaSaksnummer = "1111";
        String sverige = "SE:1234";
        String danmark = "DK:4321";

        server.expect(requestTo("/buc/" + rinaSaksnummer + "/mottakere?mottakere=" + sverige + "," + danmark))
                .andRespond(withSuccess("1234", MediaType.APPLICATION_JSON));

        euxConsumer.settMottakere(rinaSaksnummer, List.of(sverige, danmark));
    }

    @Test
    void settMottaker_respons404_kasterNotFoundException() {
        String rinaSaksnummer = "1111";
        String sverige = "SE:1234";
        String danmark = "DK:4321";
        var mottakere = List.of(sverige, danmark);

        server.expect(requestTo("/buc/" + rinaSaksnummer + "/mottakere?mottakere=" + sverige + "," + danmark))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> euxConsumer.settMottakere(rinaSaksnummer, mottakere));
    }

    @Test
    void hentRinaSak_forventUrlSomTekst() {
        String rinaSaksnummer = "1111";
        String domene = "https://rina-ss1-q.adeo.no/portal/#/caseManagement/";

        server.expect(requestTo("/url/buc/" + rinaSaksnummer))
                .andRespond(withSuccess(domene + rinaSaksnummer, MediaType.TEXT_PLAIN));

        String response = euxConsumer.hentRinaUrl(rinaSaksnummer);
        assertThat(response).isEqualTo(domene + rinaSaksnummer);
    }

    @Test
    void hentInstitusjoner_forventListe() throws Exception {

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
    void opprettBucOgSedMedVedlegg_forventString() throws Exception {
        String buc = "buc", mottaker = "NAV", filtype = "virus.exe", vedlegg = "vedlegg";
        SED sed = new SED();

        Map<String, String> forventetResultat = Maps.newHashMap();
        forventetResultat.put("documentId", "123ewq123ewq");
        forventetResultat.put("caseId", "rewf24");
        forventetResultat.put("attachmentId", "ffrewf24");

        server.expect(requestTo("/buc/sed/vedlegg?BuCType=" + buc + "&MottakerID=" + mottaker + "&FilType=" + filtype))
                .andRespond(withSuccess(objectMapper.writeValueAsString(forventetResultat), MediaType.APPLICATION_JSON));

        Map<String, String> resultat = euxConsumer.opprettBucOgSedMedVedlegg(buc, mottaker, filtype, sed, vedlegg.getBytes());
        assertThat(resultat).isEqualTo(forventetResultat);
    }

    @Test
    void finnRinaSaker_forventJson() throws Exception {
        URL jsonUrl = getClass().getClassLoader().getResource("mock/bucinfo.json");
        assertThat(jsonUrl).isNotNull();
        String forventetRetur = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));

        String bucType = "LA_BUC_04";
        String status = "ferdig";

        //Må encode uri, da non-ascii blir escaped
        String uri = UriComponentsBuilder
                .fromUriString("/rinasaker?buctype=" + bucType + "&status=" + status).toUriString();

        server.expect(requestTo(uri))
                .andRespond(withSuccess(forventetRetur, MediaType.APPLICATION_JSON));

        List<BucInfo> resultat = euxConsumer
                .finnRinaSaker(bucType, status);
        assertThat(resultat).isNotEmpty();
        assertThat(resultat.size()).isEqualTo(2);
        assertThat(resultat.get(0).getId()).isEqualTo("100485");
    }

    @Test
    void hentSedA001_forventSed() throws Exception {
        String id = "123";
        String dokumentId = "312";

        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA001.json");
        assertThat(jsonUrl).isNotNull();
        String sed = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId))
                .andRespond(withSuccess(sed, MediaType.APPLICATION_JSON));

        SED resultat = euxConsumer.hentSed(id, dokumentId);
        assertThat(resultat).isNotNull();
        assertThat(resultat.getNav()).isNotNull()
                .extracting(nav -> nav.getArbeidsgiver().get(0).getNavn()).isEqualTo("Testarbeidsgiver");
        assertThat(resultat.getMedlemskap()).isNotNull();
        assertThat(resultat.getSedType()).isEqualTo(SedType.A001.name());
        assertThat(resultat.getMedlemskap().getClass()).isEqualTo(MedlemskapA001.class);

        MedlemskapA001 medlemskapA001 = (MedlemskapA001) resultat.getMedlemskap();
        assertThat(medlemskapA001.getForrigesoeknad().get(0).getDato()).isEqualTo("2017-12-01");
    }

    @Test
    void hentSedA003_forventSed() throws Exception {
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
        assertThat(medlemskap.getVedtak().getGjelderperiode().getSluttdato()).isEqualTo("2020-02-02");
    }

    @Test
    void hentSedA008_forventSed() throws Exception {
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
    void hentSedA009_forventSed() throws Exception {
        String id = "123";
        String dokumentId = "312";

        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA009.json");
        assertThat(jsonUrl).isNotNull();
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
    void hentSedA010_forventSed() throws Exception {
        String id = "123";
        String dokumentId = "312";

        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA010.json");
        assertThat(jsonUrl).isNotNull();
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
    void hentSedX001_forventSed() throws Exception {
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
    void hentSedH001MedArbeidsgiver_forventSed() throws Exception {
        String id = "123";
        String dokumentId = "312";

        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedH001_medArbeidsgiver.json");
        assertThat(jsonUrl).isNotNull();
        String sed = IOUtils.toString(new InputStreamReader(new FileInputStream(jsonUrl.getFile())));

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId))
                .andRespond(withSuccess(sed, MediaType.APPLICATION_JSON));

        SED resultat = euxConsumer.hentSed(id, dokumentId);
        assertThat(resultat).isNotNull();
        assertThat(resultat.getNav()).isNotNull()
                .extracting(Nav::getArbeidsgiver).asList().hasSize(1);
        assertThat(resultat.getSedType()).isEqualTo(SedType.H001.name());
        assertThat(resultat.getMedlemskap()).isNull();
    }

    @Test
    void hentSedPdf_forventPdf() {
        String id = "123", dokumentId = "123321";

        byte[] forventetRetur = "teststring".getBytes();

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId + "/pdf"))
                .andRespond(withSuccess(forventetRetur, MediaType.APPLICATION_OCTET_STREAM));

        byte[] resultat = euxConsumer.hentSedPdf(id, dokumentId);
        assertThat(resultat).isEqualTo(forventetRetur);
    }

    @Test
    void genererPdfFraSed_forventPdf() {
        SED sed = new SED();
        byte[] forventetRetur = "teststring".getBytes();

        server.expect(requestTo("/sed/pdf"))
                .andRespond(withSuccess(forventetRetur, MediaType.APPLICATION_PDF));

        byte[] resultat = euxConsumer.genererPdfFraSed(sed);
        assertThat(forventetRetur).isEqualTo(resultat);
    }

    @Test
    void opprettSed_forventId() {
        String id = "123";
        SED sed = new SED();

        String forventetRetur = "123321";

        server.expect(requestTo("/buc/" + id + "/sed"))
                .andRespond(withSuccess(forventetRetur, MediaType.APPLICATION_JSON));

        String resultat = euxConsumer.opprettSed(id, sed);
        assertThat(resultat).isEqualTo(forventetRetur);
    }

    @Test
    void oppdaterSed_ingenRetur() {
        String id = "123";
        String dokumentId = "1111";
        SED sed = new SED();

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId))
                .andRespond(withSuccess());

        euxConsumer.oppdaterSed(id, dokumentId, sed);
    }

    @Test
    void sendSed_ingenRetur() {
        String id = "123";
        String dokumentId = "22";

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId + "/send"))
                .andRespond(withSuccess());

        euxConsumer.sendSed(id, dokumentId);
    }

    @Test
    void leggTilVedlegg_forventId() {
        final String id = "123";
        final String dokumentId = "123321";
        final String filtype = "virus.exe";
        final String filNavn = "filnavn123";

        final String forventetRetur = "546327ghrjek";

        server.expect(requestTo("/buc/" + id + "/sed/" + dokumentId + "/vedleggJson"))
                .andRespond(withSuccess(forventetRetur, MediaType.APPLICATION_JSON));

        String resultat = euxConsumer.leggTilVedlegg(id, dokumentId, filtype, new SedVedlegg(filNavn, "vedlegg".getBytes()));
        assertThat(resultat).isEqualTo(forventetRetur);
    }

    @Test
    void setSakSensitiv_ingenResponseEllerException() {

        String id = "123";
        server.expect(requestTo("/buc/" + id + "/sensitivsak"))
                .andRespond(withSuccess());

        euxConsumer.setSakSensitiv(id);
    }

    @Test
    void hentBucHandlinger_handlingerSomResponse() {
        String rinaSaksnummer = "123456";
        String handlinger = "[ \"Close\", \"Create\"]";

        server.expect(requestTo("/buc/" + rinaSaksnummer + "/muligeaksjoner?format=enkelt"))
            .andRespond(withSuccess(handlinger, MediaType.APPLICATION_JSON));

        Collection<String> resultat = euxConsumer.hentBucHandlinger(rinaSaksnummer);
        assertThat(resultat)
            .hasSize(2)
            .containsExactlyInAnyOrder("Close", "Create");
    }
}
