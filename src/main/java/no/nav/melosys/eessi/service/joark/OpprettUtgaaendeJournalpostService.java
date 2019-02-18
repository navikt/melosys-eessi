package no.nav.melosys.eessi.service.joark;

import com.fasterxml.jackson.databind.JsonNode;
import no.nav.dokarkivsed.api.v1.ArkiverUtgaaendeSed;
import no.nav.eessi.basis.SedSendt;
import no.nav.melosys.eessi.integration.dokarkivsed.DokarkivSedConsumer;
import no.nav.melosys.eessi.integration.dokarkivsed.OpprettUtgaaendeJournalpostResponse;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.gsak.GsakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static no.nav.melosys.eessi.service.joark.ForsendelseInformasjonMapper.createForsendelse;
import static no.nav.melosys.eessi.service.joark.ForsendelseInformasjonMapper.hoveddokument;

@Service
public class OpprettUtgaaendeJournalpostService {

    private final GsakService gsakService;
    private final DokarkivSedConsumer dokarkivSedConsumer;
    private final EuxConsumer euxConsumer;

    @Autowired
    public OpprettUtgaaendeJournalpostService(
            GsakService gsakService,
            DokarkivSedConsumer dokarkivSedConsumer, EuxConsumer euxConsumer) {
        this.dokarkivSedConsumer = dokarkivSedConsumer;
        this.euxConsumer = euxConsumer;
        this.gsakService = gsakService;
    }

    //Returnerer journalpostId. Trengs returverdi?
    public String arkiverUtgaaendeSed(SedSendt sedSendt) throws IntegrationException {

        byte[] pdf = SedDocumentStub.getPdfStub();
        Long sakId = 1L; //TODO: hente gsakId fra database
        Sak sak = gsakService.getSak(sakId);
        ReceiverInfo receiver = extractReceiverInformation(euxConsumer.hentDeltagere(sedSendt.getRinaSakId()));

        ArkiverUtgaaendeSed arkiverUtgaaendeSed = ArkiverUtgaaendeSed.builder()
                .forsendelsesinformasjon(createForsendelse(sak.getAktoerId(), sedSendt, sak, receiver))
//                .dokumentInfoVedleggListe(dokumentInfoVedleggListe(sedSendt))
                .dokumentInfoHoveddokument(hoveddokument(sedSendt.getSedType(), pdf))
                .build();

        OpprettUtgaaendeJournalpostResponse repsonse = dokarkivSedConsumer.create(arkiverUtgaaendeSed);

        return repsonse.getJournalpostId();
    }

//  TODO: venter på avklaring rundt potensielle endringer ved vedlegg til journalpost
//  private List<DokumentInfoVedlegg> dokumentInfoVedleggListe(SedSendt sedSendt) {}

    private ReceiverInfo extractReceiverInformation(JsonNode receiverResponse) {
        if (receiverResponse.isArray()) {
            for (JsonNode receiver : receiverResponse) {
                if ("CounterParty".equalsIgnoreCase(receiver.get("role").asText())) {
                    JsonNode organization = receiver.get("organisation");

                    ReceiverInfo receiverInfo = new ReceiverInfo();
                    receiverInfo.setId(organization.get("id").textValue());
                    receiverInfo.setName(organization.get("name").textValue());
                    return receiverInfo;
                }
            }
        }
        return null;
    }
}
