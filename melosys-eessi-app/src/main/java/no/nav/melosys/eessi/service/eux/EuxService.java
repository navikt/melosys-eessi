package no.nav.melosys.eessi.service.eux;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.Institusjon;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.TilegnetBuc;
import no.nav.melosys.eessi.metrikker.BucMetrikker;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedVedlegg;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.bucinfo.BucInfo;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@Primary
public class EuxService {

    private static final String RINA_URL_TEMPLATE = "/portal/#/caseManagement/";
    private static final String COUNTERPARTY = "CounterParty";
    private static final String FILTYPE_PDF = "pdf";

    private final EuxConsumer euxConsumer;
    private final BucMetrikker bucMetrikker;

    private final String rinaHostUrl;

    @Autowired
    public EuxService(EuxConsumer euxConsumer,
                      BucMetrikker bucMetrikker,
                      @Value("${melosys.integrations.rina-host-url}") String rinaHostUrl) {
        this.euxConsumer = euxConsumer;
        this.bucMetrikker = bucMetrikker;
        this.rinaHostUrl = rinaHostUrl;
    }

    public void slettBuC(String rinaSaksnummer) throws IntegrationException {
        euxConsumer.slettBuC(rinaSaksnummer);
    }

    public OpprettBucOgSedResponse opprettBucOgSed(BucType bucType,
                                                   Collection<String> mottakere,
                                                   SED sed,
                                                   Collection<SedVedlegg> vedlegg) throws IntegrationException {

        String rinaSaksnummer = euxConsumer.opprettBuC(bucType.name());
        euxConsumer.settMottakere(rinaSaksnummer, mottakere);
        String dokumentID = euxConsumer.opprettSed(rinaSaksnummer, sed);
        vedlegg.forEach(v -> leggTilVedlegg(rinaSaksnummer, dokumentID, v));
        bucMetrikker.bucOpprettet(bucType.name());
        log.info("Buc opprettet med id: {} og sed opprettet med id: {}", rinaSaksnummer, dokumentID);
        return new OpprettBucOgSedResponse(rinaSaksnummer, dokumentID);
    }

    private void leggTilVedlegg(String rinaSaksnummer, String dokumentID, SedVedlegg vedlegg) {
        String vedleggID = null;
        try {
            vedleggID = euxConsumer.leggTilVedlegg(rinaSaksnummer, dokumentID, FILTYPE_PDF, vedlegg);
        } catch (IntegrationException e) {
            //TODO: IntegrationException -> RuntimeException
        }
        log.info("Lagt til vedlegg med ID {} i rinasak {}", vedleggID, rinaSaksnummer);
    }

    public void sendSed(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        euxConsumer.sendSed(rinaSaksnummer, dokumentId);
    }

    public void oppdaterSed(String rinaSaksnummer, String dokumentId, SED sed) throws IntegrationException {
        euxConsumer.oppdaterSed(rinaSaksnummer, dokumentId, sed);
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
        return StringUtils.isEmpty(landkode) || landkode.equalsIgnoreCase(institusjon.getLandkode());
    }

    public void opprettOgSendSed(SED sed, String rinaSaksnummer) throws IntegrationException {
        String sedId = euxConsumer.opprettSed(rinaSaksnummer, sed);
        euxConsumer.sendSed(rinaSaksnummer, sedId);
        log.info("SED {} sendt i sak {}", sed.getSedType(), rinaSaksnummer);
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
        return euxConsumer.finnRinaSaker(bucSearch.getBucType(), bucSearch.getStatus());
    }

    public BUC hentBuc(String rinaSakid) throws IntegrationException {
        return euxConsumer.hentBuC(rinaSakid);
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
