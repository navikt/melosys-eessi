package no.nav.melosys.eessi.service.eux;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.integration.eux.dto.Institusjon;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.bucinfo.BucInfo;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.SedType;
import no.nav.melosys.eessi.service.caserelation.CaseRelationService;
import no.nav.melosys.eessi.service.joark.ParticipantInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class EuxService {

    private final EuxConsumer euxConsumer;
    private final CaseRelationService caseRelationService;

    @Value("${melosys.integrations.rina-host-url}")
    private String rinaHostUrl;

    @Autowired
    public EuxService(EuxConsumer euxConsumer,
                      CaseRelationService caseRelationService) {
        this.euxConsumer = euxConsumer;
        this.caseRelationService = caseRelationService;
    }

    public void slettBuC(String rinaSaksnummer) throws IntegrationException {
        euxConsumer.slettBuC(rinaSaksnummer);
    }

    public String opprettBucOgSed(Long gsakSaksnummer, String bucType, String mottakerLand, SED sed)
            throws IntegrationException, NotFoundException {

        try {
            BucAndSed bucAndSed = opprettOgLagreBucOgSed(gsakSaksnummer, bucType, mottakerLand, sed);
            return bucAndSed.getBucId();
        } catch (IntegrationException | NotFoundException ex) {
            log.error("Feil ved oppretting av buc og sed", ex);
            throw ex; //Exception må kastes igjen for å gi tilbakemelding til Melosys om at det har feilet
        }
    }

    /**
     * Oppretter buc og sed i EUX, lagrer relasjon mellom gsakSaksnummer og rinaCaseId,
     * og sender sed til EUX.
     *
     * @param gsakSaksnummer gsakSak knyttet til SED
     * @param bucType        Hvilken type buc som skal opprettes
     * @param mottakerLand   Land som skal motta SED
     * @param sed            SED som skal opprettes
     * @return rinaCaseId - id for rina-saken
     */
    public String opprettOgSendBucOgSed(Long gsakSaksnummer, String bucType, String mottakerLand, SED sed)
            throws IntegrationException, NotFoundException {

        BucAndSed bucAndSed = null;
        try {
            bucAndSed = opprettOgLagreBucOgSed(gsakSaksnummer, bucType, mottakerLand, sed);

            euxConsumer.sendSed(bucAndSed.getBucId(), "!23", bucAndSed.getSedId());
            log.info("Sed {} sendt", bucAndSed.getSedId());

        } catch (IntegrationException | NotFoundException ex) {
            log.error("Feil ved oppretting og sending av buc og sed", ex);
            if (bucAndSed != null && bucAndSed.getBucId() != null) {
                caseRelationService.deleteByRinaId(bucAndSed.getBucId());
                slettBuC(bucAndSed.getBucId());
            }
            throw ex; //Exception må kastes igjen for å gi tilbakemelding til Melosys om at det har feilet
        }
        return bucAndSed.getBucId();
    }

    private BucAndSed opprettOgLagreBucOgSed(Long gsakSaksnummer, String bucType, String mottakerLand, SED sed)
            throws NotFoundException, IntegrationException {

        Map<String, String> response = euxConsumer.opprettBucOgSed(bucType, avklarMottakerId(bucType, mottakerLand), sed);
        BucAndSed bucAndSed = new BucAndSed(response.get("caseId"), response.get("documentId"));
        log.info("Buc opprettet med id: {} og sed opprettet med id: {}", bucAndSed.getBucId(), bucAndSed.getSedId());

        caseRelationService.save(gsakSaksnummer, bucAndSed.getBucId());
        log.info("gsakSaksnummer {} lagret med rinaId {}", gsakSaksnummer, bucAndSed.getBucId());

        return bucAndSed;
    }

    private String avklarMottakerId(String bucType, String landkode) throws IntegrationException, NotFoundException {
        List<Institusjon> institusjoner = euxConsumer.hentInstitusjoner(bucType, landkode);

        return institusjoner.stream().map(Institusjon::getId).findFirst()
                .orElseThrow(() -> new NotFoundException("Finner ikke mottaker for landkode " + landkode + " og buc " + bucType));
    }

    public void opprettOgSendSed(SED sed, String rinaSaksnummer) throws IntegrationException {
        String sedId = euxConsumer.opprettSed(rinaSaksnummer, null, sed);
        euxConsumer.sendSed(rinaSaksnummer, null, sedId);
        log.info("SED {} sendt i sak {}", sed.getSed(), rinaSaksnummer);
    }

    public String opprettSed(SED sed, String rinaSaksnummer) throws IntegrationException {
        String sedId = euxConsumer.opprettSed(rinaSaksnummer, null, sed);
        log.info("SED {} opprettet i sak {}", sed.getSed(), rinaSaksnummer);

        return sedId;
    }

    /**
     * Henter mottaker (Counter Party) for en rina-sak
     *
     * @param rinaSaksnummer id for rina-saken
     * @return ParticipantInfo - inneholder id og navn for mottaker
     */
    public ParticipantInfo hentMottaker(String rinaSaksnummer) throws IntegrationException {
        return extractReceiverInformation(euxConsumer.hentDeltagere(rinaSaksnummer));
    }

    /**
     * Henter utsender (Case Owner) for en rina-sak
     *
     * @param rinaSaksnummer id for rina-saken
     * @return ParticipantInfo - inneholder id og navn for utsender
     */
    public ParticipantInfo hentUtsender(String rinaSaksnummer) throws IntegrationException {
        return extractSenderInformation(euxConsumer.hentDeltagere(rinaSaksnummer));
    }

    public SED hentSed(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        return euxConsumer.hentSed(rinaSaksnummer, dokumentId);
    }

    public List<BucInfo> hentBucer(BucSearch bucSearch) throws IntegrationException {
        return euxConsumer.finnRinaSaker(bucSearch.getFnr(), bucSearch.getFornavn(), bucSearch.getEtternavn(),
                bucSearch.getFoedselsdato(), bucSearch.getRinaSaksnummer(), bucSearch.getBucType(), bucSearch.getStatus());
    }

    public BUC hentBuc(String rinaSakid) throws IntegrationException {
        return euxConsumer.hentBuC(rinaSakid);
    }

    public byte[] hentSedPdf(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        return euxConsumer.hentSedPdf(rinaSaksnummer, dokumentId);
    }

    public boolean sedKanOpprettesPaaBuc(String rinaSaksnummer, SedType sedType) {
        try {
            return euxConsumer.hentTilgjengeligeSedTyper(rinaSaksnummer).stream()
                    .anyMatch(type -> type.equalsIgnoreCase(sedType.name()));
        } catch (IntegrationException e) {
            log.error("Kunne ikke hente tilgjengelige sed-typer for buc {}", rinaSaksnummer, e);
            return false;
        }
    }

    private static ParticipantInfo extractReceiverInformation(JsonNode participants) {
        return extractParticipantInformation(participants, "CounterParty");
    }

    private static ParticipantInfo extractSenderInformation(JsonNode participants) {
        return extractParticipantInformation(participants, "CaseOwner");
    }

    private static ParticipantInfo extractParticipantInformation(JsonNode participants, String participantRole) {
        if (participants.isArray()) {
            for (JsonNode deltager : participants) {
                if (participantRole.equalsIgnoreCase(deltager.get("role").asText())) {
                    JsonNode organization = deltager.get("organisation");

                    ParticipantInfo participantInfo = new ParticipantInfo();
                    participantInfo.setId(organization.get("id").textValue());
                    participantInfo.setName(organization.get("name").textValue());
                    return participantInfo;
                }
            }
        }

        return null;
    }

    public String hentRinaUrl(String rinaCaseId, String sedId) {

        if (StringUtils.isEmpty(rinaCaseId)) {
            throw new IllegalArgumentException("Trenger RinaSaksnummer for å opprette url til rina");
        }

        if (StringUtils.isEmpty(sedId)) {
            return hentRinaUrl(rinaCaseId);
        }

        return rinaHostUrl + "/portal/#/caseManagement/" + rinaCaseId + "?openMode=Update&docId=" + sedId;
    }

    private String hentRinaUrl(String rinaCaseId) {
        return rinaHostUrl + "/portal/#/caseManagement/" + rinaCaseId;
    }

    @Data
    @AllArgsConstructor
    private static class BucAndSed {
        private String bucId;
        private String sedId;
    }
}
