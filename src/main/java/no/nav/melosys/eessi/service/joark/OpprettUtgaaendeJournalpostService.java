package no.nav.melosys.eessi.service.joark;

import java.util.Collections;
import com.fasterxml.jackson.databind.JsonNode;
import no.nav.dokarkivsed.api.v1.*;
import no.nav.eessi.basis.SedSendt;
import no.nav.melosys.eessi.integration.dokarkivsed.DokarkivSedConsumer;
import no.nav.melosys.eessi.integration.dokarkivsed.OpprettUtgaaendeJournalpostResponse;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.ValidationException;
import no.nav.melosys.eessi.service.dokkat.DokkatService;
import no.nav.melosys.eessi.service.gsak.GsakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpprettUtgaaendeJournalpostService {

    private final OpprettUtgaaendeJournalpostMapper opprettUtgaaendeJournalpostMapper;
    private final DokkatService dokkatService;
    private final GsakService gsakService;
    private final DokarkivSedConsumer dokarkivSedConsumer;
    private final EuxConsumer euxConsumer;

    @Autowired
    public OpprettUtgaaendeJournalpostService(
            DokkatService dokkatService, GsakService gsakService,
            DokarkivSedConsumer dokarkivSedConsumer, EuxConsumer euxConsumer) {
        this.dokarkivSedConsumer = dokarkivSedConsumer;
        this.euxConsumer = euxConsumer;
        this.opprettUtgaaendeJournalpostMapper = new OpprettUtgaaendeJournalpostMapper();
        this.dokkatService = dokkatService;
        this.gsakService = gsakService;
    }

    //Returnerer journalpostId. Trengs returverdi?
    public String arkiverUtgaaendeSed(SedSendt sedSendt) throws ValidationException, IntegrationException {

        byte[] pdf = SedDocumentStub.getPdfStub();
        Long sakId = 1L; //TODO: kall mot melosys med bucId for å hent gsak-sak knyttet til rina-sak sedSendt.getRinaSakId()
        Sak sak = gsakService.getSak(sakId);
        ReceiverInfo receiver = extractReceiverInformation(euxConsumer.hentDeltagere(sedSendt.getRinaSakId()));


        ArkiverUtgaaendeSed arkiverUtgaaendeSed = ArkiverUtgaaendeSed.builder()
                .forsendelsesinformasjon(opprettUtgaaendeJournalpostMapper.createForsendelse(null, sedSendt, sak, receiver))
//                .dokumentInfoVedleggListe() TODO: vedlegg
                .dokumentInfoHoveddokument(hoveddokument(sedSendt.getSedType(), pdf))
                .build();

        OpprettUtgaaendeJournalpostResponse repsonse = dokarkivSedConsumer.create(arkiverUtgaaendeSed);
//        DokkatSedInfo dokkatSedInfo = dokkatService.hentMetadataFraDokkat(sedSendt.getSedType()); //TODO: trengs dette?

        return repsonse.getJournalpostId();
    }

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

    private static DokumentInfoHoveddokument hoveddokument(String sedType, byte[] pdf) {
        return DokumentInfoHoveddokument.builder()
                .sedType(sedType)
                .filinfoListe(Collections.singletonList(Filinfo.builder()
                        .dokument(pdf)
                        .variantFormat(VariantFormat.ARKIV)
                        .arkivFilType(ArkivFilType.PDFA)
                        .build()))
                .build();
    }

}
