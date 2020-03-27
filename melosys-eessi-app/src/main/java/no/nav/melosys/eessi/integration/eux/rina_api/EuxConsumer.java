package no.nav.melosys.eessi.integration.eux.rina_api;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.integration.UUIDGenerator;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.Institusjon;
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
import org.springframework.web.util.UriComponentsBuilder;

import static no.nav.melosys.eessi.integration.RestUtils.hentFeilmeldingForEux;

@Slf4j
public class EuxConsumer implements RestConsumer, UUIDGenerator {

    private final RestTemplate euxRestTemplate;
    private final ObjectMapper objectMapper;

    private static final String RINA_SAKSNUMMER = "rinasaksnummer";
    private static final String KORRELASJONS_ID = "KorrelasjonsId";
    private static final String BUC_TYPE = "BuCType";

    private static final String BUC_PATH = "/buc/%s";
    private static final String SED_PATH = "/buc/%s/sed/%s";
    private static final String SED_PATH_PDF = "/buc/%s/sed/%s/pdf";
    private static final String SED_MED_VEDLEGG_PATH = "/buc/%s/sed/%s/filer";
    private static final String VEDLEGG_PATH = "/buc/%s/sed/%s/vedlegg";

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

    public BUC hentBuC(String rinaSaksnummer) throws IntegrationException {

        log.info("Henter buc: {}", rinaSaksnummer);
        String uri = String.format(BUC_PATH, rinaSaksnummer);

        return exchange(uri, HttpMethod.GET, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<BUC>() {
                });
    }

    /**
     * Oppretter ny BuC/RINA-sak
     *
     * @param bucType Type BuC. Eks. LA_BUC_04
     * @return saksnummer til nye sak som er opprettet
     */

    public String opprettBuC(String bucType) throws IntegrationException {
        log.info("Oppretter buc, type: {}", bucType);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/buc")
                .queryParam(BUC_TYPE, bucType);

        return exchange(builder.toUriString(), HttpMethod.POST,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<String>() {
                });
    }

    /**
     * Sletter en BuC/Rina-sak
     *
     * @param rinaSaksnummer saksnummer til BuC'en
     */

    public void slettBuC(String rinaSaksnummer) throws IntegrationException {
        log.info("Sletter buc: {}", rinaSaksnummer);
        String uri = String.format(BUC_PATH, rinaSaksnummer);

        exchange(uri, HttpMethod.DELETE, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<Void>() {
                });
    }

    /**
     * Setter mottaker på en BuC/Rina-sak
     *
     * @param rinaSaksnummer saksnummer
     * @param mottakere id på mottakende enhet
     */
    public void settMottakere(String rinaSaksnummer, Collection<String> mottakere) throws IntegrationException {

        log.info("Setter mottaker {} til sak {}", mottakere, rinaSaksnummer);
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath(String.format("/buc/%s/mottakere", rinaSaksnummer))
                .queryParam("mottakere", mottakere.toArray());

        exchange(builder.toUriString(), HttpMethod.PUT, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<Void>() {
                });
    }

    /**
     * Henter deltagere i saken
     *
     * @return liste over deltagere
     */

    public JsonNode hentDeltagere(String rinaSaksnummer) throws IntegrationException {

        log.info("Henter deltakere til sak {}", rinaSaksnummer);
        String uri = String.format("/buc/%s/bucdeltakere", rinaSaksnummer);

        return exchange(uri, HttpMethod.GET, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<JsonNode>() {
                });
    }

    /**
     * Oppretter en SED på en eksisterende BuC
     *
     * @param rinaSaksnummer saksnummer til BuC/rina-saken
     * @param sed SED'en som skal legges til rina-saken
     * @return dokumentId' til SED'en
     */

    public String opprettSed(String rinaSaksnummer, SED sed)
            throws IntegrationException {

        log.info("Oppretter SED {} på sak {}", sed.getSedType(), rinaSaksnummer);
        String uri = UriComponentsBuilder.fromPath(String.format("/buc/%s/sed", rinaSaksnummer)).toUriString();

        return exchange(uri, HttpMethod.POST, new HttpEntity<>(sed, defaultHeaders()),
                new ParameterizedTypeReference<String>() {
                });
    }

    /**
     * Henter ut en eksisterende SED
     *
     * @param rinaSaksnummer saksnummeret hvor SED'en er tilknyttet
     * @param dokumentId id' til SED'en som skal hentes
     */

    public SED hentSed(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        log.info("Henter sed med id {}, fra sak {}", dokumentId, rinaSaksnummer);
        String uri = String.format(SED_PATH, rinaSaksnummer, dokumentId);

        return exchange(uri, HttpMethod.GET, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<SED>() {
                });
    }

    /**
     * Oppdaterer en eksisterende SED
     *
     * @param rinaSaksnummer saksnummeret
     * @param korrelasjonsId Optional, brukes ikke av eux per nå
     * @param dokumentId Id'en til SED'en som skal oppdateres
     * @param sed Den nye versjonen av SED'en
     */

    public void oppdaterSed(String rinaSaksnummer, String korrelasjonsId, String dokumentId,
            SED sed) throws IntegrationException {
        log.info("Oppdaterer sed {} på sak {}", dokumentId, rinaSaksnummer);
        String uri = UriComponentsBuilder
                .fromPath(String.format(SED_PATH, rinaSaksnummer, dokumentId))
                .queryParam(KORRELASJONS_ID, korrelasjonsId).toUriString();

        exchange(uri, HttpMethod.PUT, new HttpEntity<>(sed, defaultHeaders()),
                new ParameterizedTypeReference<Void>() {
                });
    }

    public byte[] hentSedPdf(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        log.info("Henter pdf for sed {} på sak {}", dokumentId, rinaSaksnummer);
        String uri = String.format(SED_PATH_PDF, rinaSaksnummer, dokumentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

        return exchange(uri, HttpMethod.GET, new HttpEntity<>(headers),
                new ParameterizedTypeReference<byte[]>() {}
                );
    }

    public byte[] genererPdfFraSed(SED sed) throws IntegrationException {
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
     * @param korrelasjonsId optional, ikke brukt av eux per nå
     */

    public void sendSed(String rinaSaksnummer, String korrelasjonsId, String dokumentId)
            throws IntegrationException {
        log.info("Sender sed {} fra sak {}", dokumentId, rinaSaksnummer);
        String uri = UriComponentsBuilder
                .fromPath(String.format(SED_PATH, rinaSaksnummer, dokumentId) + "/send")
                .queryParam(KORRELASJONS_ID, korrelasjonsId).toUriString();

        exchange(uri, HttpMethod.POST, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<Void>() {
                });
    }

    /**
     * Legger til et vedlegg for et dokument
     *
     * @param rinaSaksnummer saksnummeret
     * @param dokumentId id til SED'en vedlegget skal legges til
     * @param filType filtype
     * @param vedlegg Selve vedlegget som skal legges til
     * @return ukjent
     */

    public String leggTilVedlegg(String rinaSaksnummer, String dokumentId, String filType,
            Object vedlegg) throws IntegrationException {
        log.info("Legger til vedlegg på sak {} og dokument {}", rinaSaksnummer, dokumentId);
        String uri = UriComponentsBuilder
                .fromPath(String.format(VEDLEGG_PATH, rinaSaksnummer, dokumentId))
                .queryParam("Filtype", filType).toUriString();

        return exchange(uri, HttpMethod.POST, new HttpEntity<>(vedlegg, defaultHeaders()),
                new ParameterizedTypeReference<String>() {
                });
    }

    /**
     * Setter en sak sensitiv
     *
     * @param rinaSaksnummer saksnummeret
     */

    public void setSakSensitiv(String rinaSaksnummer) throws IntegrationException {
        log.info("Setter sak {} sensitiv", rinaSaksnummer);
        String uri = String.format(BUC_PATH, rinaSaksnummer) + "/sensitivsak";

        exchange(uri, HttpMethod.PUT, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<Void>() {
                });
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
            SED sed, byte[] vedlegg) throws IntegrationException {
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

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("document", document);
        map.add("attachment", attachment);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/buc/sed/vedlegg")
                .queryParam(BUC_TYPE, bucType)
                .queryParam("MottakerID", mottakerId)
                .queryParam("FilType", filType);

        return exchange(builder.toUriString(), HttpMethod.POST, new HttpEntity<>(map, headers),
                new ParameterizedTypeReference<Map<String, String>>() {
                });
    }

    /**
     * Henter en liste over registrerte institusjoner innenfor spesifiserte EU-land
     *
     * @param bucType BuC/Rina-saksnummer
     * @param landkode kode til landet det skal hente institusjoner fra
     */

    @Cacheable("institusjoner")
    public List<Institusjon> hentInstitusjoner(String bucType, String landkode)
            throws IntegrationException {
        log.info("Henter institusjoner for buctype {} og landkode {}", bucType, landkode);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/institusjoner")
                .queryParam(BUC_TYPE, bucType)
                .queryParam("LandKode", landkode);

        return exchange(builder.toUriString(), HttpMethod.GET,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<List<Institusjon>>() {
                });
    }

    /**
     * Søker etter rina-saker etter gitte parametere. Alle parametere er optional
     *
     * @param fnr fødselsnummer
     * @param fornavn fornavn
     * @param etternavn etternavn
     * @param foedselsdato fødselsdato
     * @param rinaSaksnummer rinaSaksnummer
     * @param bucType bucType
     * @param status status
     * @return JsonNode med rina saker
     */

    public List<BucInfo> finnRinaSaker(String fnr, String fornavn, String etternavn, String foedselsdato,
            String rinaSaksnummer, String bucType, String status) throws IntegrationException {
        log.info("Søker etter rina-saker");
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/rinasaker")
                .queryParam("fødselsnummer", fnr)
                .queryParam("fornavn", fornavn)
                .queryParam("etternavn", etternavn)
                .queryParam("fødselsdato", foedselsdato)
                .queryParam(RINA_SAKSNUMMER, rinaSaksnummer)
                .queryParam("buctype", bucType)
                .queryParam("status", status);

        //Må vurdere å endre returverdi til en POJO om denne integrasjonen faktisk tas i bruk
        return exchange(builder.build(false).toUriString(), HttpMethod.GET,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<List<BucInfo>>() {
                });
    }

    public SedMedVedlegg hentSedMedVedlegg(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        log.info("Henter SED med vedlegg for sak {} og sed {}", rinaSaksnummer, dokumentId);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath(String.format(SED_MED_VEDLEGG_PATH, rinaSaksnummer, dokumentId));

        return exchange(builder.toUriString(), HttpMethod.GET,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<SedMedVedlegg>() {
                });
    }

    private <T> T exchange(String uri, HttpMethod method, HttpEntity<?> entity,
            ParameterizedTypeReference<T> responseType) throws IntegrationException {
        try {
            return euxRestTemplate.exchange(uri, method, entity, responseType).getBody();
        } catch (RestClientException e) {
            throw new IntegrationException("Error in integration with eux: " + hentFeilmeldingForEux(e), e);
        }
    }
}
