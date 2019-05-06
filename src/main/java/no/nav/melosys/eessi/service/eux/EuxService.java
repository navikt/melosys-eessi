package no.nav.melosys.eessi.service.eux;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.integration.eux.dto.Institusjon;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.caserelation.CaseRelationService;
import no.nav.melosys.eessi.service.joark.ParticipantInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    /**
     * Oppretter buc og sed i EUX, lagrer relasjon mellom gsakSaksnummer og rinaCaseId,
     * og sender sed til EUX.
     * @param gsakSaksnummer gsakSak knyttet til SED
     * @param bucType Hvilken type buc som skal opprettes
     * @param mottakerLand Land som skal motta SED
     * @param sed SED som skal opprettes
     * @return rinaCaseId - id for rina-saken
     */
    public String opprettOgSendBucOgSed(Long gsakSaksnummer, String bucType, String mottakerLand, SED sed)
            throws IntegrationException, NotFoundException {

        String rinaId = null;
        String documentId;

        try {
            Map<String, String> map = euxConsumer
                    .opprettBucOgSed(bucType, avklarMottakerId(bucType, mottakerLand), sed);
            rinaId = map.get("caseId");
            documentId = map.get("documentId");
            log.info("Buc opprettet med id: {} og sed opprettet med id: {}", rinaId, documentId);

            caseRelationService.save(gsakSaksnummer, rinaId);
            log.info("gsakSaksnummer {} lagret med rinaId {}", gsakSaksnummer, rinaId);

            euxConsumer.sendSed(rinaId, "!23", documentId);
            log.info("Sed {} sendt", documentId);

        } catch (IntegrationException|NotFoundException ex) {
            log.error("Feil ved oppretting og sending av buc og sed", ex);
            if (rinaId != null) {
                caseRelationService.deleteByRinaId(rinaId);
                slettBuC(rinaId);
            }
            throw ex; //Exception må kastes igjen for å gi tilbakemelding til Melosys om at det har feilet
        }
        return rinaId;
    }

    private String avklarMottakerId(String bucType, String landkode) throws IntegrationException, NotFoundException {
        List<Institusjon> institusjoner = euxConsumer.hentInstitusjoner(bucType, landkode);

        return institusjoner.stream().map(i -> i.getId().split(":")[1]).findFirst()
                .orElseThrow(() -> new NotFoundException("Finner ikke mottaker for landkode " + landkode + " og buc " + bucType));
    }

    /**
     * Henter mottaker (Counter Party) for en rina-sak
     * @param rinaSaksnummer id for rina-saken
     * @return ParticipantInfo - inneholder id og navn for mottaker
     */
    public ParticipantInfo hentMottaker(String rinaSaksnummer) throws IntegrationException {
        return extractReceiverInformation(euxConsumer.hentDeltagere(rinaSaksnummer));
    }

    /**
     * Henter utsender (Case Owner) for en rina-sak
     * @param rinaSaksnummer id for rina-saken
     * @return ParticipantInfo - inneholder id og navn for utsender
     */
    public ParticipantInfo hentUtsender(String rinaSaksnummer) throws IntegrationException {
        return extractSenderInformation(euxConsumer.hentDeltagere(rinaSaksnummer));
    }

    public SED hentSed(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        return euxConsumer.hentSed(rinaSaksnummer, dokumentId);
    }

    public byte[] hentSedPdf(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        return euxConsumer.hentSedPdf(rinaSaksnummer, dokumentId);
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
        return "https://" + rinaHostUrl + "/portal/#/caseManagement/" + rinaCaseId +
                "?openMode=Update&docId=" + sedId;
    }

    public String hentRinaUrl(String rinaCaseId) {
        return "https://" + rinaHostUrl + "/portal/#/caseManagement/" + rinaCaseId;
    }
}
