package no.nav.melosys.eessi.integration.eux.rina_api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.integration.UUIDGenerator;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.Institusjon;
import no.nav.melosys.eessi.models.SedVedlegg;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.bucinfo.BucInfo;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static no.nav.melosys.eessi.integration.RestUtils.hentFeilmeldingForEux;

@Slf4j
public class EuxConsumer implements RestConsumer, UUIDGenerator {

    private final RestTemplate euxRestTemplate;
    private final ObjectMapper objectMapper;

    private static final String BUC_PATH = "/buc/{rinaSaksnummer}";
    private static final String SED_PATH = "/buc/{rinaSaksnummer}/sed/{rinaDokumentID}";
    private static final String SED_PATH_PDF = "/buc/{rinaSaksnummer}/sed/{rinaDokumentID}/pdf";
    private static final String SED_MED_VEDLEGG_PATH = "/buc/{rinaSaksnummer}/sed/{rinaDokumentID}/filer";
    private static final String VEDLEGG_PATH = "/buc/{rinaSaksnummer}/sed/{rinaDokumentID}/vedlegg";

    public EuxConsumer(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.euxRestTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Henter ut eksisterende BuC
     *
     * @param rinaSaksnummer Saksnummer til BuC
     * @return JsonNode klasse. Selve returverdien er et svært komplisert objekt, derfor er ikke det
     * spesifikke objektet spesifisert
     */

    public BUC hentBuC(String rinaSaksnummer) {

        log.info("Henter buc: {}", rinaSaksnummer);

        return exchange(BUC_PATH, HttpMethod.GET, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<BUC>() {}, rinaSaksnummer);
    }

    /**
     * Oppretter ny BuC/RINA-sak
     *
     * @param bucType Type BuC. Eks. LA_BUC_04
     * @return saksnummer til nye sak som er opprettet
     */

    public String opprettBuC(String bucType) {
        log.info("Oppretter buc, type: {}", bucType);

        return exchange("/buc?BuCType={bucType}", HttpMethod.POST,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<String>() {},
                bucType);
    }

    /**
     * Sletter en BuC/Rina-sak
     *
     * @param rinaSaksnummer saksnummer til BuC'en
     */

    public void slettBuC(String rinaSaksnummer) {
        log.info("Sletter buc: {}", rinaSaksnummer);

        exchange(BUC_PATH, HttpMethod.DELETE, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<Void>() {}, rinaSaksnummer);
    }

    /**
     * Setter mottaker på en BuC/Rina-sak
     *
     * @param rinaSaksnummer saksnummer
     * @param mottakerIDer id på mottakende enhet
     */
    public void settMottakere(String rinaSaksnummer, Collection<String> mottakerIDer) {

        log.info("Setter mottaker {} til sak {}", mottakerIDer, rinaSaksnummer);

        exchange("/buc/{rinaSaksnummer}/mottakere?mottakere={mottakere}", HttpMethod.PUT, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<Void>() {}, rinaSaksnummer, mottakerIDer.toArray());
    }

    /**
     * Oppretter en SED på en eksisterende BuC
     *
     * @param rinaSaksnummer saksnummer til BuC/rina-saken
     * @param sed SED'en som skal legges til rina-saken
     * @return dokumentId' til SED'en
     */

    public String opprettSed(String rinaSaksnummer, SED sed) {
        log.info("Oppretter SED {} på sak {}", sed.getSedType(), rinaSaksnummer);

        return exchange("/buc/{rinaSaksnummer}/sed", HttpMethod.POST, new HttpEntity<>(sed, defaultHeaders()),
                new ParameterizedTypeReference<String>() {}, rinaSaksnummer);
    }

    /**
     * Henter ut en eksisterende SED
     *
     * @param rinaSaksnummer saksnummeret hvor SED'en er tilknyttet
     * @param dokumentId id' til SED'en som skal hentes
     */

    public SED hentSed(String rinaSaksnummer, String dokumentId) {
        log.info("Henter sed med id {}, fra sak {}", dokumentId, rinaSaksnummer);

        return exchange(SED_PATH, HttpMethod.GET, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<SED>() {}, rinaSaksnummer, dokumentId);
    }

    /**
     * Oppdaterer en eksisterende SED
     *
     * @param rinaSaksnummer saksnummeret
     * @param dokumentId Id'en til SED'en som skal oppdateres
     * @param sed Den nye versjonen av SED'en
     */

    public void oppdaterSed(String rinaSaksnummer, String dokumentId, SED sed) {
        log.info("Oppdaterer sed {} på sak {}", dokumentId, rinaSaksnummer);

        exchange(SED_PATH, HttpMethod.PUT, new HttpEntity<>(sed, defaultHeaders()),
                new ParameterizedTypeReference<Void>() {}, rinaSaksnummer, dokumentId);
    }

    public byte[] hentSedPdf(String rinaSaksnummer, String dokumentId) {
        log.info("Henter pdf for sed {} på sak {}", dokumentId, rinaSaksnummer);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

        return exchange(SED_PATH_PDF, HttpMethod.GET, new HttpEntity<>(headers),
                new ParameterizedTypeReference<byte[]>() {}, rinaSaksnummer, dokumentId);
    }

    public byte[] genererPdfFraSed(SED sed) {
        log.info("Henter pdf for forhåndsvisning av sed med type {}", sed.getSedType());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));

        return exchange("/sed/pdf", HttpMethod.POST, new HttpEntity<>(sed, headers),
                new ParameterizedTypeReference<byte[]>() {}
        );
    }


    /**
     * Sender en SED til mottakkere. Mottakere må være satt før den kan sendes.
     *
     * @param rinaSaksnummer saksnummeret
     * @param dokumentId id' til SED som skal sendes
     */

    public void sendSed(String rinaSaksnummer, String dokumentId) {
        log.info("Sender sed {} fra sak {}", dokumentId, rinaSaksnummer);

        exchange(SED_PATH + "/send", HttpMethod.POST, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<Void>() {}, rinaSaksnummer, dokumentId);
    }

    /**
     * Legger til et vedlegg for et dokument
     *
     * @param rinaSaksnummer saksnummeret
     * @param dokumentId id til SED'en vedlegget skal legges til
     * @param filType filtype (eks pdf)
     * @param vedlegg Selve vedlegget som skal legges til
     * @return ukjent
     */
    public String leggTilVedlegg(String rinaSaksnummer, String dokumentId, String filType,
                                 SedVedlegg vedlegg) {
        log.info("Legger til vedlegg på sak {} og dokument {}", rinaSaksnummer, dokumentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ByteArrayResource document = new ByteArrayResource(vedlegg.getInnhold()) {
            @Override
            public String getFilename() {
                return vedlegg.getTittel();
            }
        };

        MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();
        multipartBody.add("file", document);

        return exchange(VEDLEGG_PATH + "?Filtype={filType}&Filnavn={filnavn}&synkron={synkron}", HttpMethod.POST, new HttpEntity<>(multipartBody, headers),
                new ParameterizedTypeReference<String>() {}, rinaSaksnummer, dokumentId, filType, URLEncoder.encode(vedlegg.getTittel(), StandardCharsets.UTF_8), Boolean.TRUE);
    }

    /**
     * Setter en sak sensitiv
     *
     * @param rinaSaksnummer saksnummeret
     */

    public void setSakSensitiv(String rinaSaksnummer) {
        log.info("Setter sak {} sensitiv", rinaSaksnummer);

        exchange(BUC_PATH + "/sensitivsak", HttpMethod.PUT, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<Void>() {}, rinaSaksnummer);
    }

    /**
     * Oppretter en BuC med en tilhørende SED og evt vedlegg
     *
     * @param bucType Hvilken type buc som skal opprettes. Eks LA_BUC_04
     * @param mottakerId Mottaker sin Rina-id
     * @param filType filtype til vedlegg
     * @param sed sed'en som skal opprettes
     * @param vedlegg vedlegget som skal legges til saken
     * @return @return id til rina-sak, id til dokument og id til vedlegg som ble opprettet. Nøkler:
     * caseId, documentId og attachmentId
     */

    public Map<String, String> opprettBucOgSedMedVedlegg(String bucType, String mottakerId, String filType,
            SED sed, byte[] vedlegg) {
        log.info("Oppretter buc {}, med sed {}, med mottaker {} og legger til vedlegg. ", bucType,
                sed.getSedType(), mottakerId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        byte[] documentBytes;

        try {
            documentBytes = objectMapper.writeValueAsBytes(sed);
        } catch (JsonProcessingException jpe) {
            throw new IntegrationException("Feil ved opprettelse av SED mot EUX", jpe);
        }

        ByteArrayResource document = new ByteArrayResource(documentBytes) {
            @Override
            public String getFilename() {
                return "document";
            }
        };
        ByteArrayResource attachment = new ByteArrayResource(vedlegg) {
            @Override
            public String getFilename() {
                return "attachment";
            }
        };

        MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();
        multipartBody.add("document", document);
        multipartBody.add("attachment", attachment);

        return exchange("/buc/sed/vedlegg?BuCType={bucType}&MottakerID={mottakerID}&FilType={filType}",
                HttpMethod.POST, new HttpEntity<>(multipartBody, headers),
                new ParameterizedTypeReference<Map<String, String>>() {}, bucType, mottakerId, filType);
    }

    /**
     * Henter en liste over registrerte institusjoner innenfor spesifiserte EU-land
     *
     * @param bucType BuC/Rina-saksnummer
     * @param landkode kode til landet det skal hente institusjoner fra
     */

    @Cacheable("institusjoner")
    public List<Institusjon> hentInstitusjoner(String bucType, String landkode) {
        log.info("Henter institusjoner for buctype {} og landkode {}", bucType, landkode);

        return exchange("/institusjoner?BuCType={bucType}&LandKode={landkode}", HttpMethod.GET,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<List<Institusjon>>() {},
                bucType, landkode);
    }

    public List<BucInfo> finnRinaSaker(String bucType, String status) {
        log.info("Søker etter rina-saker med buctype {} og status {}", bucType, status);

        return exchange("/rinasaker?buctype={buctype}&status={status}", HttpMethod.GET,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<List<BucInfo>>() {},
                bucType, status);
    }

    public SedMedVedlegg hentSedMedVedlegg(String rinaSaksnummer, String dokumentId) {
        log.info("Henter SED med vedlegg for sak {} og sed {}", rinaSaksnummer, dokumentId);

        return exchange(SED_MED_VEDLEGG_PATH, HttpMethod.GET,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<SedMedVedlegg>() {},
                rinaSaksnummer, dokumentId);
    }

    private <T> T exchange(String uri, HttpMethod method, HttpEntity<?> entity,
            ParameterizedTypeReference<T> responseType, Object... variabler) {
        try {
            return euxRestTemplate.exchange(uri, method, entity, responseType, variabler).getBody();
        } catch (RestClientException e) {
            throw new IntegrationException("Error in integration with eux: " + hentFeilmeldingForEux(e), e);
        }
    }
}
