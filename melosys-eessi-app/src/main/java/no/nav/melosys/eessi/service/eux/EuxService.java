package no.nav.melosys.eessi.service.eux;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.Institusjon;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.TilegnetBuc;
import no.nav.melosys.eessi.metrikker.BucMetrikker;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.bucinfo.BucInfo;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class EuxService {

    private static final String RINA_URL_TEMPLATE = "/portal/#/caseManagement/";
    private static final String COUNTERPARTY = "CounterParty";
    private static final String FILTYPE_PDF = "pdf";

    private final EuxConsumer euxConsumer;
    private final BucMetrikker bucMetrikker;

    private final String rinaHostUrl;
    private final String mottakerInstitusjon;

    @Autowired
    public EuxService(EuxConsumer euxConsumer,
            BucMetrikker bucMetrikker,
            @Value("${melosys.integrations.rina-host-url}") String rinaHostUrl,
            @Value("${MOTTAKER_INSTITUSJON:}") String mottakerInstitusjon) {
        this.euxConsumer = euxConsumer;
        this.bucMetrikker = bucMetrikker;
        this.rinaHostUrl = rinaHostUrl;
        this.mottakerInstitusjon = mottakerInstitusjon;
    }

    public void slettBuC(String rinaSaksnummer) throws IntegrationException {
        euxConsumer.slettBuC(rinaSaksnummer);
    }

    public OpprettBucOgSedResponse opprettBucOgSed(String bucType, String mottakerLand, String mottakerId, SED sed, byte[] vedlegg)
            throws IntegrationException, NotFoundException {

        if (StringUtils.isEmpty(mottakerId)) {
            mottakerId = avklarMottakerId(bucType, mottakerLand);
        }

        Map<String, String> response;
        if (vedlegg != null && vedlegg.length > 0) {
            response = euxConsumer.opprettBucOgSedMedVedlegg(bucType, mottakerId, FILTYPE_PDF, sed, vedlegg);
        } else {
            response = euxConsumer.opprettBucOgSed(bucType, mottakerId, sed);
        }
        OpprettBucOgSedResponse opprettBucOgSedResponse = new OpprettBucOgSedResponse(response.get("caseId"),
                response.get("documentId"));
        log.info("Buc opprettet med id: {} og sed opprettet med id: {}", opprettBucOgSedResponse.getRinaSaksnummer(),
                opprettBucOgSedResponse.getDokumentId());
        bucMetrikker.bucOpprettet(bucType);

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

    public List<Institusjon> hentMottakerinstitusjoner(final String bucType, final String landkode)
            throws IntegrationException {

        return euxConsumer.hentInstitusjoner(bucType, null).stream()
                .peek(i -> i.setLandkode(LandkodeMapper.mapTilNavLandkode(i.getLandkode())))
                .filter(i -> filtrerPåLandkode(i, landkode))
                .filter(i -> i.getTilegnetBucs().stream().filter(
                        tilegnetBuc -> bucType.equals(tilegnetBuc.getBucType()) &&
                                COUNTERPARTY.equals(tilegnetBuc.getInstitusjonsrolle()))
                        .anyMatch(TilegnetBuc::erEessiKlar))
                .collect(Collectors.toList());
    }

    private boolean filtrerPåLandkode(Institusjon institusjon, String landkode) {
        return landkode == null || landkode.equalsIgnoreCase(institusjon.getLandkode());
    }

    public void opprettOgSendSed(SED sed, String rinaSaksnummer) throws IntegrationException {
        String sedId = euxConsumer.opprettSed(rinaSaksnummer, null, sed);
        euxConsumer.sendSed(rinaSaksnummer, null, sedId);
        log.info("SED {} sendt i sak {}", sed.getSed(), rinaSaksnummer);
    }

    public boolean sedErEndring(String sedId, String rinaSaksnummer) throws IntegrationException {
        BUC buc = euxConsumer.hentBuC(rinaSaksnummer);

        return buc.getDocuments().stream()
                .filter(document -> document.getId().equals(sedId)).findFirst()
                .filter(document -> document.getConversations().size() > 1).isPresent();
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

    public SedMedVedlegg hentSedMedVedlegg(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        return euxConsumer.hentSedMedVedlegg(rinaSaksnummer, dokumentId);
    }

    public byte[] genererPdfFraSed(SED sed) throws IntegrationException {
        return euxConsumer.genererPdfFraSed(sed);
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

    public void settSakSensitiv(String rinaSaksnummer) throws IntegrationException {
        euxConsumer.setSakSensitiv(rinaSaksnummer);
    }
}
