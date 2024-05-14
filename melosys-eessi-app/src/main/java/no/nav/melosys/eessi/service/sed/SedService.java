package no.nav.melosys.eessi.service.sed;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.getunleash.Unleash;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.config.featuretoggle.ToggleName;
import no.nav.melosys.eessi.controller.dto.BucOgSedOpprettetDto;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.SedJournalstatus;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.SedVedlegg;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.ValidationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.eux.OpprettBucOgSedResponse;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import no.nav.melosys.eessi.service.sed.helpers.SedMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static no.nav.melosys.eessi.models.buc.SedVersjonSjekker.verifiserSedVersjonErBucVersjon;

@Slf4j
@Service
public class SedService {

    private final EuxService euxService;
    private final SaksrelasjonService saksrelasjonService;

    private final Unleash unleash;

    @Autowired
    public SedService(@Qualifier("tokenContext") EuxService euxService,
                      SaksrelasjonService saksrelasjonService, Unleash unleash) {
        this.euxService = euxService;
        this.saksrelasjonService = saksrelasjonService;
        this.unleash = unleash;
    }

    public BucOgSedOpprettetDto opprettBucOgSed(SedDataDto sedDataDto,
                                                Collection<SedVedlegg> vedlegg,
                                                BucType bucType,
                                                boolean sendAutomatisk,
                                                boolean forsøkOppdaterEksisterende)
        throws ValidationException {

        Long gsakSaksnummer = hentGsakSaksnummer(sedDataDto);
        log.info("Oppretter buc og sed, gsakSaksnummer: {}", gsakSaksnummer);

        Collection<String> mottakere = sedDataDto.getMottakerIder();
        var sedType = bucType.hentFørsteLovligeSed();
        var sedMapper = SedMapperFactory.sedMapper(sedType);
        var sed = sedMapper.mapTilSed(sedDataDto, unleash.isEnabled(ToggleName.CDM_4_3));

        validerMottakerInstitusjoner(bucType, mottakere);

        OpprettBucOgSedResponse response = opprettEllerOppdaterBucOgSed(
            sed, vedlegg, bucType, gsakSaksnummer, sedDataDto.getMottakerIder(), forsøkOppdaterEksisterende
        );

        if (sedDataDto.getBruker().isHarSensitiveOpplysninger()) {
            euxService.settSakSensitiv(response.getRinaSaksnummer());
        }

        if (sedType.name().startsWith("H")) {
            euxService.settSedJournalstatus(response.getRinaSaksnummer(), tilUUIDMedBindestreker(response.getDokumentId()), 0, SedJournalstatus.MELOSYS_JOURNALFOERER);
        }

        if (sendAutomatisk) {
            sendSed(response.getRinaSaksnummer(), response.getDokumentId(), sed.getSedType());
        }

        return BucOgSedOpprettetDto.builder()
            .rinaSaksnummer(response.getRinaSaksnummer())
            .rinaUrl(euxService.hentRinaUrl(response.getRinaSaksnummer()))
            .build();
    }

    private String tilUUIDMedBindestreker(String uuidString){
        return UUID.fromString(
            uuidString.substring(0, 8) + "-" +
                uuidString.substring(8, 12) + "-" +
                uuidString.substring(12, 16) + "-" +
                uuidString.substring(16, 20) + "-" +
                uuidString.substring(20)
        ).toString();
    }
    private void validerMottakerInstitusjoner(BucType bucType, Collection<String> mottakere) throws ValidationException {
        if (mottakere.isEmpty()) {
            throw new ValidationException("Mottakere er påkrevd");
        } else if (!bucType.erMultilateralLovvalgBuc() && mottakere.size() > 1) {
            throw new ValidationException(bucType + " kan ikke ha flere mottakere!");
        }
    }

    /*
    NB! Må legge inn en sleep grunnet problematikk i RINA.
    Ved opprettelse av SED og umiddelbar sending rett etter, *kan* Rina-saken bli "skadd" ved opprettelse.
    Det kan videre føre til at vi ikke kan se svar-SEDer vi mottar fra utlandet, som vil påvirke tiden det tar før vi får ut vedtak til bruker
     */
    private void sendSed(String rinaSaksnummer, String dokumentId, String sedType) {
        try {
            TimeUnit.SECONDS.sleep(10L);
            euxService.sendSed(rinaSaksnummer, dokumentId, sedType);
        } catch (IntegrationException e) {
            log.error("Feil ved oppretting og/eller sending av buc og sed. Exception fanges for å slette saksrelasjon.");
            slettBucOgSaksrelasjon(rinaSaksnummer);
            throw e;
        } catch (InterruptedException e) {
            log.error("Uventet InterruptedException", e);
            Thread.currentThread().interrupt();
            slettBucOgSaksrelasjon(rinaSaksnummer);
            throw new RuntimeException(e);
        }
    }

    private void slettBucOgSaksrelasjon(String rinaSaksnummer) {
        euxService.slettBUC(rinaSaksnummer);
        saksrelasjonService.slettVedRinaId(rinaSaksnummer);
    }

    public byte[] genererPdfFraSed(SedDataDto sedDataDto, SedType sedType) {
        var sedMapper = SedMapperFactory.sedMapper(sedType);
        var sed = sedMapper.mapTilSed(sedDataDto, unleash.isEnabled(ToggleName.CDM_4_3));


        return euxService.genererPdfFraSed(sed);
    }

    public void sendPåEksisterendeBuc(SedDataDto sedDataDto, String rinaSaksnummer, SedType sedType) {
        var buc = euxService.hentBuc(rinaSaksnummer);

        var sed = SedMapperFactory.sedMapper(sedType).mapTilSed(sedDataDto, unleash.isEnabled(ToggleName.CDM_4_3));
        verifiserSedVersjonErBucVersjon(buc, sed);
        euxService.opprettOgSendSed(sed, rinaSaksnummer);
    }

    private OpprettBucOgSedResponse opprettEllerOppdaterBucOgSed(SED sed,
                                                                 Collection<SedVedlegg> vedlegg,
                                                                 BucType bucType,
                                                                 Long gsakSaksnummer,
                                                                 List<String> mottakerIder,
                                                                 boolean forsøkOppdaterEksisterende) {
        if (forsøkOppdaterEksisterende && bucType.meddelerLovvalg()) {
            Optional<BUC> eksisterendeSak = finnAapenEksisterendeSak(
                saksrelasjonService.finnVedGsakSaksnummerOgBucType(gsakSaksnummer, bucType)
            );

            if (eksisterendeSak.isPresent() && eksisterendeSak.get().erÅpen()) {
                var buc = eksisterendeSak.get();
                Optional<Document> document = buc.finnDokumentVedSedType(sed.getSedType());

                if (document.isPresent() && buc.sedKanOppdateres(document.get().getId())) {
                    String rinaSaksnummer = buc.getId();
                    String dokumentId = document.get().getId();
                    verifiserSedVersjonErBucVersjon(buc, sed);
                    log.info("SED {} på rinasak {} oppdateres", dokumentId, rinaSaksnummer);
                    euxService.oppdaterSed(rinaSaksnummer, dokumentId, sed);
                    return new OpprettBucOgSedResponse(rinaSaksnummer, dokumentId);
                }
            }
        }

        return opprettOgLagreSaksrelasjon(sed, vedlegg, bucType, gsakSaksnummer, mottakerIder);
    }

    private Optional<BUC> finnAapenEksisterendeSak(List<FagsakRinasakKobling> eksisterendeSaker) {
        for (FagsakRinasakKobling fagsakRinasakKobling : eksisterendeSaker) {
            var buc = euxService.finnBUC(fagsakRinasakKobling.getRinaSaksnummer());
            if (buc.isPresent() && buc.get().erÅpen()) {
                return buc;
            }
        }

        return Optional.empty();
    }

    private OpprettBucOgSedResponse opprettOgLagreSaksrelasjon(SED sed, Collection<SedVedlegg> vedlegg, BucType bucType, Long gsakSaksnummer, List<String> mottakerIder) {
        var opprettBucOgSedResponse = euxService.opprettBucOgSed(bucType, mottakerIder, sed, vedlegg);
        saksrelasjonService.lagreKobling(gsakSaksnummer, opprettBucOgSedResponse.getRinaSaksnummer(), bucType);
        log.info("gsakSaksnummer {} lagret med rinaId {}", gsakSaksnummer, opprettBucOgSedResponse.getRinaSaksnummer());
        return opprettBucOgSedResponse;
    }

    private static Long hentGsakSaksnummer(SedDataDto sedDataDto) {
        return Optional.ofNullable(sedDataDto.getGsakSaksnummer()).orElseThrow(() -> new MappingException("GsakId er påkrevd!"));
    }
}
