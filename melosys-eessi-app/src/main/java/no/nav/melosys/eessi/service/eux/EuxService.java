package no.nav.melosys.eessi.service.eux;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.eux.rina_api.Aksjoner;
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.Institusjon;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.TilegnetBuc;
import no.nav.melosys.eessi.metrikker.BucMetrikker;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedVedlegg;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.bucinfo.BucInfo;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.exception.ValidationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@Primary
public class EuxService {

    private static final String COUNTERPARTY = "CounterParty";
    private static final String FILTYPE_PDF = "pdf";

    private final EuxConsumer euxConsumer;
    private final BucMetrikker bucMetrikker;


    @Autowired
    public EuxService(EuxConsumer euxConsumer,
                      BucMetrikker bucMetrikker) {
        this.euxConsumer = euxConsumer;
        this.bucMetrikker = bucMetrikker;
    }

    public void slettBUC(String rinaSaksnummer) {
        euxConsumer.slettBUC(rinaSaksnummer);
    }

    public OpprettBucOgSedResponse opprettBucOgSed(BucType bucType,
                                                   Collection<String> mottakere,
                                                   SED sed,
                                                   Collection<SedVedlegg> vedlegg) {

        String rinaSaksnummer = euxConsumer.opprettBUC(bucType.name());
        euxConsumer.settMottakere(rinaSaksnummer, mottakere);
        String dokumentID = euxConsumer.opprettSed(rinaSaksnummer, sed);
        vedlegg.forEach(v -> leggTilVedlegg(rinaSaksnummer, dokumentID, v));
        bucMetrikker.bucOpprettet(bucType.name());
        log.info("Buc opprettet med id: {} og sed opprettet med id: {}", rinaSaksnummer, dokumentID);
        return new OpprettBucOgSedResponse(rinaSaksnummer, dokumentID);
    }

    private void leggTilVedlegg(String rinaSaksnummer, String dokumentID, SedVedlegg vedlegg) {
        String vedleggID = euxConsumer.leggTilVedlegg(rinaSaksnummer, dokumentID, FILTYPE_PDF, vedlegg);
        log.info("Lagt til vedlegg med ID {} i rinasak {}", vedleggID, rinaSaksnummer);
    }

    public void sendSed(String rinaSaksnummer, String dokumentId, String sedType) {
        validerSedHandling(rinaSaksnummer, dokumentId, Aksjoner.SEND);
        euxConsumer.sendSed(rinaSaksnummer, dokumentId);
        log.info("SED {} sendt i sak {}", sedType, rinaSaksnummer);
    }

    public void oppdaterSed(String rinaSaksnummer, String dokumentId, SED sed) {
        euxConsumer.oppdaterSed(rinaSaksnummer, dokumentId, sed);
    }

    public List<Institusjon> hentMottakerinstitusjoner(final String bucType, final Collection<String> landkoder) {

        return euxConsumer.hentInstitusjoner(bucType, null).stream()
            .peek(i -> i.setLandkode(LandkodeMapper.mapTilNavLandkode(i.getLandkode())))
            .filter(i -> filtrerPåLandkoder(i, landkoder))
            .filter(i -> i.getTilegnetBucs().stream().filter(
                    tilegnetBuc -> bucType.equals(tilegnetBuc.getBucType()) &&
                        COUNTERPARTY.equals(tilegnetBuc.getInstitusjonsrolle()))
                .anyMatch(TilegnetBuc::erEessiKlar))
            .collect(Collectors.toList());
    }

    private boolean filtrerPåLandkoder(Institusjon institusjon, Collection<String> landkoder) {
        return landkoder.isEmpty() || landkoder.stream()
            .map(String::toLowerCase)
            .anyMatch(landkode -> landkode.equalsIgnoreCase(institusjon.getLandkode()));
    }

    public void opprettOgSendSed(SED sed, String rinaSaksnummer) {
        validerBucHandling(rinaSaksnummer, Aksjoner.CREATE);
        String sedId = euxConsumer.opprettSed(rinaSaksnummer, sed);
        validerSedHandling(rinaSaksnummer, sedId, Aksjoner.SEND);

        euxConsumer.sendSed(rinaSaksnummer, sedId);
        log.info("SED {} sendt i sak {}", sed.getSedType(), rinaSaksnummer);
    }

    public void validerBucHandling(String rinaSaksnummer, Aksjoner aksjon) {
        if (!bucHandlingErMulig(rinaSaksnummer, aksjon)) {
            throw new ValidationException(String.format("Kan ikke gjøre handling %s på BUC %s" +
                ", ugyldig handling i Rina", aksjon.hentHandling(), rinaSaksnummer));
        }
    }

    public void validerSedHandling(String rinaSaksnummer, String sedId, Aksjoner aksjon) {
        if (!sedHandlingErMulig(rinaSaksnummer, sedId, aksjon)) {
            throw new ValidationException(String.format("Kan ikke sende SED på BUC %s, ugyldig handling %s i Rina",
                rinaSaksnummer, aksjon.hentHandling()));
        }
    }

    public boolean sedErEndring(String sedId, String rinaSaksnummer) {
        var buc = euxConsumer.hentBUC(rinaSaksnummer);

        return buc.getDocuments().stream()
            .filter(document -> document.getId().equals(sedId)).findFirst()
            .filter(document -> document.getConversations().size() > 1).isPresent();
    }

    public SED hentSed(String rinaSaksnummer, String dokumentId) {
        return euxConsumer.hentSed(rinaSaksnummer, dokumentId);
    }

    @Retryable
    public SED hentSedMedRetry(String rinaSaksnummer, String dokumentId) {
        return hentSed(rinaSaksnummer, dokumentId);
    }

    public List<BucInfo> hentBucer(BucSearch bucSearch) {
        return euxConsumer.finnRinaSaker(bucSearch.getBucType(), bucSearch.getStatus());
    }

    public BUC hentBuc(String rinaSaksnummer) {
        return euxConsumer.hentBUC(rinaSaksnummer);
    }

    /**
     * Kaster ikke exception om en BUC er arkivert eller ikke finnes
     */
    public Optional<BUC> finnBUC(String rinaSaksnummer) {
        try {
            return Optional.of(euxConsumer.hentBUC(rinaSaksnummer));
        } catch (IntegrationException | NotFoundException e) {
            log.warn("Kan ikke hente BUC {}", rinaSaksnummer, e);
            return Optional.empty();
        }
    }

    public boolean bucHandlingErMulig(String rinaSaksnummer, Aksjoner aksjon) {
        return euxConsumer.hentBucHandlinger(rinaSaksnummer)
            .stream().anyMatch(s -> s.split(" ")[2].equals(aksjon.hentHandling()));
    }

    public boolean sedHandlingErMulig(String rinaSaksnummer, String dokumentId, Aksjoner handling) {
        return euxConsumer.hentSedHandlinger(rinaSaksnummer, dokumentId)
            .stream().anyMatch(s -> s.equals(handling.hentHandling()));
    }

    public SedMedVedlegg hentSedMedVedlegg(String rinaSaksnummer, String dokumentId) {
        return euxConsumer.hentSedMedVedlegg(rinaSaksnummer, dokumentId);
    }

    public byte[] genererPdfFraSed(SED sed) {
        return euxConsumer.genererPdfFraSed(sed);
    }

    public String hentRinaUrl(String rinaCaseId) {
        if (!StringUtils.hasText(rinaCaseId)) {
            throw new IllegalArgumentException("Trenger rina-saksnummer for å opprette url til rina");
        }
        return euxConsumer.hentRinaUrl(rinaCaseId);
    }

    public void settSakSensitiv(String rinaSaksnummer) {
        euxConsumer.setSakSensitiv(rinaSaksnummer);
    }
}
