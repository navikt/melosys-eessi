package no.nav.melosys.eessi.service.sed;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.OpprettSedDto;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.Vedlegg;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.exception.ValidationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.eux.OpprettBucOgSedResponse;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import no.nav.melosys.eessi.service.sed.helpers.SedMapperFactory;
import no.nav.melosys.eessi.service.sed.mapper.til_sed.SedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SedService {

    private final EuxService euxService;
    private final SaksrelasjonService saksrelasjonService;

    @Autowired
    public SedService(@Qualifier("tokenContext") EuxService euxService,
                      SaksrelasjonService saksrelasjonService) {
        this.euxService = euxService;
        this.saksrelasjonService = saksrelasjonService;
    }

    public OpprettSedDto opprettBucOgSed(SedDataDto sedDataDto, Vedlegg vedlegg, BucType bucType, boolean sendAutomatisk)
            throws MappingException, IntegrationException, NotFoundException, ValidationException {

        Long gsakSaksnummer = hentGsakSaksnummer(sedDataDto);
        log.info("Oppretter buc og sed, gsakSaksnummer: {}", gsakSaksnummer);

        Collection<String> mottakere = sedDataDto.getMottakerIder();
        SedType sedType = bucType.hentFørsteLovligeSed();
        SedMapper sedMapper = SedMapperFactory.sedMapper(sedType);
        SED sed = sedMapper.mapTilSed(sedDataDto);

        validerMottakerInstitusjoner(bucType, mottakere);

        OpprettBucOgSedResponse response = opprettEllerOppdaterBucOgSed(
                sed, vedlegg, bucType, gsakSaksnummer, sedDataDto.getMottakerIder()
        );

        if (sedDataDto.getBruker().isHarSensitiveOpplysninger()) {
            euxService.settSakSensitiv(response.getRinaSaksnummer());
        }

        if (sendAutomatisk) {
            sendSed(response.getRinaSaksnummer(), response.getDokumentId());
        }

        return OpprettSedDto.builder()
                .rinaSaksnummer(response.getRinaSaksnummer())
                .rinaUrl(euxService.hentRinaUrl(response.getRinaSaksnummer()))
                .build();
    }

    private void validerMottakerInstitusjoner(BucType bucType, Collection<String> mottakere) throws ValidationException {
        if (mottakere.isEmpty()) {
            throw new ValidationException("Mottakere er påkrevd");
        } else if (!bucType.erMultilateralLovvalgBuc() && mottakere.size() > 1) {
            throw new ValidationException(bucType + " kan ikke ha flere mottakere!");
        }
    }

    private void sendSed(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        try {
            euxService.sendSed(rinaSaksnummer, dokumentId);
        } catch (IntegrationException e) {
            log.error("Feil ved oppretting og/eller sending av buc og sed. Exception fanges for å slette saksrelasjon.");
            euxService.slettBuC(rinaSaksnummer);
            saksrelasjonService.slettVedRinaId(rinaSaksnummer);
            throw e;
        }
    }

    public byte[] genererPdfFraSed(SedDataDto sedDataDto, SedType sedType) throws MappingException, NotFoundException, IntegrationException {
        SedMapper sedMapper = SedMapperFactory.sedMapper(sedType);
        SED sed = sedMapper.mapTilSed(sedDataDto);

        return euxService.genererPdfFraSed(sed);
    }

    public void sendPåEksisterendeBuc(SedDataDto sedDataDto, String rinaSaksnummer, SedType sedType) throws MappingException, NotFoundException, IntegrationException {
        BUC buc = euxService.hentBuc(rinaSaksnummer);
        if (!buc.kanOppretteSed(sedType)) {
            throw new IllegalArgumentException("Kan ikke opprette sed med type " + sedType + " på buc "+ rinaSaksnummer + " med type " + buc.getBucType());
        }

        SED sed = SedMapperFactory.sedMapper(sedType).mapTilSed(sedDataDto);
        euxService.opprettOgSendSed(sed, rinaSaksnummer);
    }

    private OpprettBucOgSedResponse opprettEllerOppdaterBucOgSed(SED sed, Vedlegg vedlegg, BucType bucType, Long gsakSaksnummer, List<String> mottakerIder) throws IntegrationException {

        if (bucType.meddelerLovvalg()) {
            Optional<BUC> eksisterendeSak = finnAapenEksisterendeSak(
                    saksrelasjonService.finnVedGsakSaksnummerOgBucType(gsakSaksnummer, bucType)
            );

            if (eksisterendeSak.isPresent() && eksisterendeSak.get().erÅpen()) {
                BUC buc = eksisterendeSak.get();
                Optional<Document> document = buc.finnDokumentVedSedType(sed.getSedType());

                if (document.isPresent() && buc.sedKanOppdateres(document.get().getId())) {
                    String rinaSaksnummer = buc.getId();
                    String dokumentId = document.get().getId();
                    log.info("SED {} på rinasak {} oppdateres", dokumentId, rinaSaksnummer);
                    euxService.oppdaterSed(rinaSaksnummer, dokumentId, sed);
                    return new OpprettBucOgSedResponse(rinaSaksnummer, dokumentId);
                }
            }
        }

        return opprettOgLagreSaksrelasjon(sed, vedlegg, bucType, gsakSaksnummer, mottakerIder);
    }

    private Optional<BUC> finnAapenEksisterendeSak(List<FagsakRinasakKobling> eksisterendeSaker) throws IntegrationException {
        for (FagsakRinasakKobling fagsakRinasakKobling : eksisterendeSaker) {
            BUC buc = euxService.hentBuc(fagsakRinasakKobling.getRinaSaksnummer());
            if ("open".equals(buc.getStatus())) {
                return Optional.of(buc);
            }
        }

        return Optional.empty();
    }

    private OpprettBucOgSedResponse opprettOgLagreSaksrelasjon(SED sed, Vedlegg vedlegg, BucType bucType, Long gsakSaksnummer, List<String> mottakerIder)
            throws IntegrationException {
        OpprettBucOgSedResponse opprettBucOgSedResponse = euxService.opprettBucOgSed(bucType, mottakerIder, sed, vedlegg);
        saksrelasjonService.lagreKobling(gsakSaksnummer, opprettBucOgSedResponse.getRinaSaksnummer(), bucType);
        log.info("gsakSaksnummer {} lagret med rinaId {}", gsakSaksnummer, opprettBucOgSedResponse.getRinaSaksnummer());
        return opprettBucOgSedResponse;
    }

    private static Long hentGsakSaksnummer(SedDataDto sedDataDto) throws MappingException {
        return Optional.ofNullable(sedDataDto.getGsakSaksnummer()).orElseThrow(() -> new MappingException("GsakId er påkrevd!"));
    }
}
