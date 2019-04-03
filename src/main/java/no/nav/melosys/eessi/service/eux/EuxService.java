package no.nav.melosys.eessi.service.eux;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.caserelation.CaseRelationService;
import no.nav.melosys.eessi.service.joark.ParticipantInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EuxService {

    private final EuxConsumer euxConsumer;
    private final CaseRelationService caseRelationService;

    @Autowired
    public EuxService(EuxConsumer euxConsumer,
                      CaseRelationService caseRelationService) {
        this.euxConsumer = euxConsumer;
        this.caseRelationService = caseRelationService;
    }

    public JsonNode hentBuC(String rinaSaksnummer) throws IntegrationException {
        return euxConsumer.hentBuC(rinaSaksnummer);
    }

    public String opprettBuC(String bucType) throws IntegrationException {
        return euxConsumer.opprettBuC(bucType);
    }

    public void slettBuC(String rinaSaksnummer) throws IntegrationException {
        euxConsumer.slettBuC(rinaSaksnummer);
    }

    public void settMottaker(String rinaSaksnummer, String mottakerId) throws IntegrationException {
        euxConsumer.settMottaker(rinaSaksnummer, mottakerId);
    }

    /**
     * Oppretter buc og sed i EUX, lagrer relasjon mellom gsakSaksnummer og rinaCaseId,
     * og sender sed til EUX.
     * @param gsakSaksnummer gsakSak knyttet til Sed
     * @param bucType Hvilken type buc som skal opprettes
     * @param mottakerId Id til mottaker (Counter Party)
     * @param sed Sed som skal opprettes
     * @return rinaCaseId - id for rina-saken
     */
    public String opprettOgSendBucOgSed(Long gsakSaksnummer, String bucType, String mottakerId, SED sed) throws IntegrationException {

        Map<String, String> map = euxConsumer.opprettBucOgSed(bucType, mottakerId, sed);
        String rinaCaseId = map.get("caseId");
        String documentId = map.get("documentId");
        log.info("Buc opprettet med id: {} og sed opprettet med id: {}", rinaCaseId, documentId);

        caseRelationService.save(gsakSaksnummer, rinaCaseId);
        log.info("gsakSaksnummer {} lagret med rinaId {}", gsakSaksnummer, rinaCaseId);

        euxConsumer.sendSed(rinaCaseId, "!23", documentId);
        log.info("Sed {} sendt", documentId);

        return rinaCaseId;
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

    public JsonNode hentMuligeAksjoner(String rinaSaksnummer) throws IntegrationException {
        return euxConsumer.hentMuligeAksjoner(rinaSaksnummer);
    }

    public String opprettSed(String rinaSaksnummer, String korrelasjonsId, SED sed) throws IntegrationException {
        return euxConsumer.opprettSed(rinaSaksnummer, korrelasjonsId, sed);
    }

    public SED hentSed(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        return euxConsumer.hentSed(rinaSaksnummer, dokumentId);
    }

    public byte[] hentSedPdf(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        return euxConsumer.hentSedPdf(rinaSaksnummer, dokumentId);
    }

    public void oppdaterSed(String rinaSaksnummer, String korrelasjonsId, String dokumentId, SED sed) throws IntegrationException {
        euxConsumer.opprettSed(rinaSaksnummer, korrelasjonsId, sed);
    }

    public void slettSed(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        euxConsumer.slettSed(rinaSaksnummer, dokumentId);
    }

    public void sendSed(String rinaSaksnummer, String korrelasjonsId, String dokumentId) throws IntegrationException {
        euxConsumer.sendSed(rinaSaksnummer, korrelasjonsId, dokumentId);
    }

    public String leggTilVedlegg(String rinaSaksnummer, String dokumentId, String filType, Object vedlegg) throws IntegrationException {
        return euxConsumer.leggTilVedlegg(rinaSaksnummer, dokumentId, filType, vedlegg);
    }

    public byte[] hentVedlegg(String rinaSaksnummer, String dokumentId, String vedleggId) throws IntegrationException {
        return euxConsumer.hentVedlegg(rinaSaksnummer, dokumentId, vedleggId);
    }

    public void slettVedlegg(String rinaSaksnummer, String dokumentId, String vedleggId) throws IntegrationException {
        euxConsumer.slettVedlegg(rinaSaksnummer, dokumentId, vedleggId);
    }

    public List<String> hentTilgjengeligeSedTyper(String rinaSaksnummer) throws IntegrationException {
        return euxConsumer.hentTilgjengeligeSedTyper(rinaSaksnummer);
    }

    public void setSakSensitiv(String rinaSaksnummer) throws IntegrationException {
        euxConsumer.setSakSensitiv(rinaSaksnummer);
    }

    public void fjernSakSensitiv(String rinaSaksnummer) throws IntegrationException {
        euxConsumer.fjernSakSensitiv(rinaSaksnummer);
    }

    public Map<String, String> opprettBucOgSed(String bucType, String mottakerId, SED sed) throws IntegrationException {
        return euxConsumer.opprettBucOgSed(bucType, mottakerId, sed);
    }

    public Map<String, String> opprettBucOgSedMedVedlegg(String bucType, String fagSakNummer, String mottakerId, String filType, String korrelasjonsId, SED sed, Object vedlegg) throws IntegrationException {
        return euxConsumer.opprettBucOgSedMedVedlegg(bucType, fagSakNummer, mottakerId, filType, korrelasjonsId, sed, vedlegg);
    }

    public List<String> bucTypePerSektor() throws IntegrationException {
        return euxConsumer.bucTypePerSektor();
    }

    public List<String> hentInstitusjoner(String bucType, String landkode) throws IntegrationException {
        return euxConsumer.hentInstitusjoner(bucType, landkode);
    }

    public JsonNode hentKodeverk(String kodeverk) throws IntegrationException {
        return euxConsumer.hentKodeverk(kodeverk);
    }

    public JsonNode finnRinaSaker(String fnr, String fornavn, String etternavn, String foedselsdato, String rinaSaksnummer, String bucType, String status) throws IntegrationException {
        return euxConsumer.finnRinaSaker(fnr, fornavn, etternavn, foedselsdato, rinaSaksnummer, bucType, status);
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
}
