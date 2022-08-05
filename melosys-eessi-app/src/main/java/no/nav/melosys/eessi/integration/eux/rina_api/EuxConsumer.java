package no.nav.melosys.eessi.integration.eux.rina_api;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.EuxVedlegg;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.Institusjon;
import no.nav.melosys.eessi.models.SedVedlegg;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.bucinfo.BucInfo;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static no.nav.melosys.eessi.integration.RestUtils.hentFeilmeldingForEux;

@Slf4j
public class EuxConsumer implements RestConsumer {

    private final RestTemplate euxRestTemplate;
    private final ObjectMapper objectMapper;

    private static final String BUC_PATH = "/buc/{rinaSaksnummer}";
    private static final String SED_PATH = "/buc/{rinaSaksnummer}/sed/{rinaDokumentID}";
    private static final String SED_PATH_PDF = "/buc/{rinaSaksnummer}/sed/{rinaDokumentID}/pdf";
    private static final String SED_MED_VEDLEGG_PATH = "/buc/{rinaSaksnummer}/sed/{rinaDokumentID}/filer";
    private static final String VEDLEGG_PATH = "/buc/{rinaSaksnummer}/sed/{rinaDokumentID}/vedleggJson";
    private static final String SED_HANDLINGER = "/buc/{rinaSaksnummer}/sed/{sedId}/handlinger";
    private static final String BUC_HANDLINGER = "/buc/{rinaSaksnummer}/muligeaksjoner";
    private static final String RINA_LENKE_PATH = "/url/buc/{rinaSaksnummer}";

    public EuxConsumer(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.euxRestTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public BUC hentBUC(String rinaSaksnummer) {
        log.info("Henter buc: {}", rinaSaksnummer);
        return exchange(BUC_PATH, HttpMethod.GET, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<>() {}, rinaSaksnummer);
    }

    public String opprettBUC(String bucType) {
        log.info("Oppretter buc, type: {}", bucType);

        return exchange("/buc?BuCType={bucType}", HttpMethod.POST,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<>() {},
                bucType);
    }

    public void slettBUC(String rinaSaksnummer) {
        log.info("Sletter buc: {}", rinaSaksnummer);
        exchange(BUC_PATH, HttpMethod.DELETE, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<Void>() {},
                rinaSaksnummer);
    }

    @Retryable(
            value = NotFoundException.class,
            backoff = @Backoff(delay = 2000, maxDelay = 4000, multiplier = 2),
            maxAttempts = 5)
    public void settMottakere(String rinaSaksnummer, Collection<String> mottakerIDer) {

        log.info("Setter mottaker {} til sak {}", mottakerIDer, rinaSaksnummer);

        exchange("/buc/{rinaSaksnummer}/mottakere?mottakere={mottakere}", HttpMethod.PUT, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<Void>() {},
                rinaSaksnummer, mottakerIDer.toArray());
    }

    public String opprettSed(String rinaSaksnummer, SED sed) {
        log.info("Oppretter SED {} på sak {}", sed.getSedType(), rinaSaksnummer);

        return exchange("/buc/{rinaSaksnummer}/sed", HttpMethod.POST,
                new HttpEntity<>(sed, defaultHeaders()),
                new ParameterizedTypeReference<>() {},
                rinaSaksnummer);
    }

    public SED hentSed(String rinaSaksnummer, String dokumentId) {
        log.info("Henter sed med id {}, fra sak {}", dokumentId, rinaSaksnummer);

        return exchange(SED_PATH, HttpMethod.GET,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<>() {},
                rinaSaksnummer, dokumentId);
    }

    public void oppdaterSed(String rinaSaksnummer, String dokumentId, SED sed) {
        log.info("Oppdaterer sed {} på sak {}", dokumentId, rinaSaksnummer);

        exchange(SED_PATH, HttpMethod.PUT,
                new HttpEntity<>(sed, defaultHeaders()),
                new ParameterizedTypeReference<Void>() {},
                rinaSaksnummer, dokumentId);
    }

    public byte[] hentSedPdf(String rinaSaksnummer, String dokumentId) {
        log.info("Henter pdf for sed {} på sak {}", dokumentId, rinaSaksnummer);

        var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

        return exchange(SED_PATH_PDF, HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {},
                rinaSaksnummer, dokumentId);
    }

    public byte[] genererPdfFraSed(SED sed) {
        log.info("Henter pdf for forhåndsvisning av sed med type {}", sed.getSedType());

        var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));

        return exchange("/sed/pdf", HttpMethod.POST,
                new HttpEntity<>(sed, headers),
                new ParameterizedTypeReference<>() {}
        );
    }

    public void sendSed(String rinaSaksnummer, String dokumentId) {
        log.info("Sender sed {} fra sak {}", dokumentId, rinaSaksnummer);

        exchange(SED_PATH + "/send", HttpMethod.POST,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<Void>() {},
                rinaSaksnummer, dokumentId);
    }

    public String leggTilVedlegg(String rinaSaksnummer, String dokumentId, String filType,
                                 SedVedlegg vedlegg) {
        log.info("Legger til vedlegg på sak {} og dokument {}", rinaSaksnummer, dokumentId);

        var headers = defaultHeaders();
        String base64Content = Base64.getEncoder().encodeToString(vedlegg.getInnhold());
        var body = new EuxVedlegg(base64Content, filType, vedlegg.getTittel(), true);

        return exchange(VEDLEGG_PATH, HttpMethod.POST,
            new HttpEntity<>(body, headers),
            new ParameterizedTypeReference<>() {},
            rinaSaksnummer, dokumentId);
    }

    public void setSakSensitiv(String rinaSaksnummer) {
        log.info("Setter sak {} sensitiv", rinaSaksnummer);

        exchange(BUC_PATH + "/sensitivsak", HttpMethod.PUT,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<Void>() {},
                rinaSaksnummer);
    }

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
                HttpMethod.POST,
                new HttpEntity<>(multipartBody, headers),
                new ParameterizedTypeReference<>() {},
                bucType, mottakerId, filType);
    }

    @Cacheable("institusjoner")
    public List<Institusjon> hentInstitusjoner(String bucType, String landkode) {
        log.info("Henter institusjoner for buctype {} og landkode {}", bucType, landkode);

        return exchange("/institusjoner?BuCType={bucType}&LandKode={landkode}", HttpMethod.GET,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<>() {},
                bucType, landkode);
    }

    public List<BucInfo> finnRinaSaker(String bucType, String status) {
        log.info("Søker etter rina-saker med buctype {} og status {}", bucType, status);

        return exchange("/rinasaker?buctype={buctype}&status={status}", HttpMethod.GET,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<>() {
                },
                bucType, status);
    }

    public String hentRinaUrl(String rinaSaksnummer) {
        log.info("Søker etter rina-url mot Rina med saksnummer {}", rinaSaksnummer);

        return exchange(RINA_LENKE_PATH, HttpMethod.GET,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<>() {
                },
                rinaSaksnummer);
    }

    public SedMedVedlegg hentSedMedVedlegg(String rinaSaksnummer, String dokumentId) {
        log.info("Henter SED med vedlegg for sak {} og sed {}", rinaSaksnummer, dokumentId);

        return exchange(SED_MED_VEDLEGG_PATH, HttpMethod.GET,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<>() {
                },
                rinaSaksnummer, dokumentId);
    }

    public Collection<String> hentSedHandlinger(String rinaSaksnummer, String sedId) {
        log.info("Henter handlinger for SED for sak {} og sed {}", rinaSaksnummer, sedId);

        return exchange(SED_HANDLINGER, HttpMethod.GET,
            new HttpEntity<>(defaultHeaders()),
            new ParameterizedTypeReference<>() {
            },
            rinaSaksnummer, sedId);
    }

    public Collection<String> hentBucHandlinger(String rinaSaksnummer) {
        log.info("Henter handlinger for BUC {}", rinaSaksnummer);

        return exchange(BUC_HANDLINGER + "?format={format}", HttpMethod.GET,
            new HttpEntity<>(defaultHeaders()),
            new ParameterizedTypeReference<>() {
            },
            rinaSaksnummer, "enkelt");
    }

    private <T> T exchange(String uri, HttpMethod method, HttpEntity<?> entity,
                           ParameterizedTypeReference<T> responseType, Object... variabler) {
        try {
            return euxRestTemplate.exchange(uri, method, entity, responseType, variabler).getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("404 fra eux: " + hentFeilmeldingForEux(e), e);
        } catch (RestClientException e) {
            throw new IntegrationException("Feil i integrasjon mot eux: " + hentFeilmeldingForEux(e), e);
        }
    }
}
