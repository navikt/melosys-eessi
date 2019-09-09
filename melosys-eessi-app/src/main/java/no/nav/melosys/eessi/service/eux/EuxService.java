package no.nav.melosys.eessi.service.eux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.integration.eux.dto.Institusjon;
import no.nav.melosys.eessi.integration.eux.dto.TilegnetBuc;
import no.nav.melosys.eessi.metrikker.MetrikkerRegistrering;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.bucinfo.BucInfo;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.joark.DeltakerInformasjon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class EuxService {

    private static final String RINA_URL_TEMPLATE = "/portal/#/caseManagement/";
    private static final String COUNTERPARTY = "CounterParty";
    private static final String CASEOWNER = "CaseOwner";


    private final EuxConsumer euxConsumer;
    private final MetrikkerRegistrering metrikkerRegistrering;

    private final String rinaHostUrl;
    private final String mottakerInstitusjon;

    @Autowired
    public EuxService(EuxConsumer euxConsumer,
            MetrikkerRegistrering metrikkerRegistrering,
            @Value("${melosys.integrations.rina-host-url}") String rinaHostUrl,
            @Value("${MOTTAKER_INSTITUSJON:}") String mottakerInstitusjon) {
        this.euxConsumer = euxConsumer;
        this.metrikkerRegistrering = metrikkerRegistrering;
        this.rinaHostUrl = rinaHostUrl;
        this.mottakerInstitusjon = mottakerInstitusjon;
    }

    public void slettBuC(String rinaSaksnummer) throws IntegrationException {
        euxConsumer.slettBuC(rinaSaksnummer);
    }

    public OpprettBucOgSedResponse opprettBucOgSed(String bucType, String mottakerLand, String mottakerId, SED sed)
            throws IntegrationException, NotFoundException {

        if (StringUtils.isEmpty(mottakerId)) {
            mottakerId = avklarMottakerId(bucType, mottakerLand);
        }

        Map<String, String> response = euxConsumer.opprettBucOgSed(bucType, mottakerId, sed);
        OpprettBucOgSedResponse opprettBucOgSedResponse = new OpprettBucOgSedResponse(response.get("caseId"),
                response.get("documentId"));
        log.info("Buc opprettet med id: {} og sed opprettet med id: {}", opprettBucOgSedResponse.getRinaSaksnummer(),
                opprettBucOgSedResponse.getDokumentId());
        metrikkerRegistrering.bucOpprettet(bucType);

        return opprettBucOgSedResponse;
    }

    public void sendSed(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        euxConsumer.sendSed(rinaSaksnummer, null, dokumentId);
    }

    public void oppdaterSed(String rinaSaksnummer, String dokumentId, SED sed) throws IntegrationException {
        euxConsumer.oppdaterSed(rinaSaksnummer, null, dokumentId, sed);
    }

    private String avklarMottakerId(String bucType, String landkode) throws IntegrationException, NotFoundException {
        if (!StringUtils.isEmpty(mottakerInstitusjon)) {
            return mottakerInstitusjon;
        }
        List<Institusjon> institusjoner = hentMottakerinstitusjoner(bucType, landkode);

        return institusjoner.stream().map(Institusjon::getId).findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Finner ikke mottaker for landkode " + landkode + " og buc " + bucType));
    }

    public List<Institusjon> hentMottakerinstitusjoner(final String bucType, String landkode)
            throws IntegrationException {
        Stream<Institusjon> institusjoner = euxConsumer.hentInstitusjoner(bucType, null).stream();

        if (!StringUtils.isEmpty(landkode)) {
            institusjoner = institusjoner.filter(institusjon -> landkode.equalsIgnoreCase(institusjon.getLandkode()));
        }

        return institusjoner.filter(i ->
                i.getTilegnetBucs().stream().filter(
                        tilegnetBuc -> bucType.equals(tilegnetBuc.getBucType()) &&
                                COUNTERPARTY.equals(tilegnetBuc.getInstitusjonsrolle()))
                        .anyMatch(TilegnetBuc::erEessiKlar))
                .collect(Collectors.toList());
    }

    public void opprettOgSendSed(SED sed, String rinaSaksnummer) throws IntegrationException {
        String sedId = euxConsumer.opprettSed(rinaSaksnummer, null, sed);
        euxConsumer.sendSed(rinaSaksnummer, null, sedId);
        log.info("SED {} sendt i sak {}", sed.getSed(), rinaSaksnummer);
    }

    String opprettSed(SED sed, String rinaSaksnummer) throws IntegrationException {
        String sedId = euxConsumer.opprettSed(rinaSaksnummer, null, sed);
        log.info("SED {} opprettet i sak {}", sed.getSed(), rinaSaksnummer);

        return sedId;
    }

    public boolean sedErEndring(String sedId, String rinaSaksnummer) throws IntegrationException {
        BUC buc = euxConsumer.hentBuC(rinaSaksnummer);

        return buc.getDocuments().stream()
                .filter(document -> document.getId().equals(sedId)).findFirst()
                .filter(document -> document.getConversations().size() > 1).isPresent();
    }

    /**
     * Henter mottaker (Counter Party) for en rina-sak
     *
     * @param rinaSaksnummer id for rina-saken
     * @return DeltakerInformasjon - inneholder id og navn for mottaker
     */
    DeltakerInformasjon hentMottaker(String rinaSaksnummer) throws IntegrationException {
        return extractReceiverInformation(euxConsumer.hentDeltagere(rinaSaksnummer));
    }

    /**
     * Henter utsender (Case Owner) for en rina-sak
     *
     * @param rinaSaksnummer id for rina-saken
     * @return DeltakerInformasjon - inneholder id og navn for utsender
     */
    DeltakerInformasjon hentUtsender(String rinaSaksnummer) throws IntegrationException {
        return extractSenderInformation(euxConsumer.hentDeltagere(rinaSaksnummer));
    }

    public SED hentSed(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        return euxConsumer.hentSed(rinaSaksnummer, dokumentId);
    }

    public List<BucInfo> hentBucer(BucSearch bucSearch) throws IntegrationException {
        return euxConsumer.finnRinaSaker(bucSearch.getFnr(), bucSearch.getFornavn(), bucSearch.getEtternavn(),
                bucSearch.getFoedselsdato(), bucSearch.getRinaSaksnummer(), bucSearch.getBucType(),
                bucSearch.getStatus());
    }

    public BUC hentBuc(String rinaSakid) throws IntegrationException {
        return euxConsumer.hentBuC(rinaSakid);
    }

    public byte[] hentSedPdf(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        return euxConsumer.hentSedPdf(rinaSaksnummer, dokumentId);
    }

    public byte[] genererPdfFraSed(SED sed) throws IntegrationException {
        return euxConsumer.genererPdfFraSed(sed);
    }

    boolean sedKanOpprettesPaaBuc(String rinaSaksnummer, SedType sedType) {
        try {
            return euxConsumer.hentTilgjengeligeSedTyper(rinaSaksnummer).stream()
                    .anyMatch(type -> type.equalsIgnoreCase(sedType.name()));
        } catch (IntegrationException e) {
            log.error("Kunne ikke hente tilgjengelige sed-typer for buc {}", rinaSaksnummer, e);
            return false;
        }
    }

    private static DeltakerInformasjon extractReceiverInformation(JsonNode participants) {
        return extractParticipantInformation(participants, COUNTERPARTY);
    }

    private static DeltakerInformasjon extractSenderInformation(JsonNode participants) {
        return extractParticipantInformation(participants, CASEOWNER);
    }

    private static DeltakerInformasjon extractParticipantInformation(JsonNode participants, String participantRole) {
        if (participants.isArray()) {
            for (JsonNode deltager : participants) {
                if (participantRole.equalsIgnoreCase(deltager.get("role").asText())) {
                    JsonNode organization = deltager.get("organisation");

                    DeltakerInformasjon deltakerInformasjon = new DeltakerInformasjon();
                    deltakerInformasjon.setId(organization.get("id").textValue());
                    deltakerInformasjon.setName(organization.get("name").textValue());
                    return deltakerInformasjon;
                }
            }
        }

        return null;
    }

    public String hentRinaUrl(String rinaCaseId) {
        if (StringUtils.isEmpty(rinaCaseId)) {
            throw new IllegalArgumentException("Trenger RinaSaksnummer for å opprette url til rina");
        }
        return rinaHostUrl + RINA_URL_TEMPLATE + rinaCaseId;
    }

    public String hentRinaUrlPrefix() {
        return rinaHostUrl + RINA_URL_TEMPLATE;
    }
}
